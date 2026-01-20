package kr.spot.core.point.application.event

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kr.spot.common.api.exception.GeneralException
import kr.spot.common.event.contract.PointReason
import kr.spot.common.event.contract.StreakMileStone
import kr.spot.common.event.payload.AttendanceCheckedEvent
import kr.spot.common.event.payload.MemberCreatedEvent
import kr.spot.common.id.IdGenerator
import kr.spot.core.point.domain.Point
import kr.spot.core.point.domain.PointHistory
import kr.spot.core.point.infrastrcuture.PointHistoryRepository
import kr.spot.core.point.infrastrcuture.PointRepository
import kr.spot.core.point.infrastrcuture.querydsl.PointCustomRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
@DisplayName("PointApplicationEventListener")
class PointApplicationEventListenerTest {
    @MockK
    private lateinit var idGenerator: IdGenerator

    @MockK
    private lateinit var pointRepository: PointRepository

    @MockK
    private lateinit var pointHistoryRepository: PointHistoryRepository

    @MockK
    private lateinit var pointCustomRepository: PointCustomRepository

    @InjectMockKs
    private lateinit var sut: PointApplicationEventListener

    @Nested
    @DisplayName("registerPoint 메서드는")
    inner class RegisterPoint {
        @Test
        fun `MemberCreatedEvent 수신 시 Point를 생성한다`() {
            // given
            val memberId = 1L
            val event = MemberCreatedEvent(memberId = memberId)
            val pointSlot = slot<Point>()

            every { idGenerator.nextId() } returns 100L
            every { pointRepository.save(capture(pointSlot)) } answers { pointSlot.captured }

            // when
            sut.registerPoint(event)

            // then
            verify(exactly = 1) { pointRepository.save(any()) }
            assertThat(pointSlot.captured.memberId).isEqualTo(memberId)
            assertThat(pointSlot.captured.amount).isEqualTo(0L)
        }
    }

    @Nested
    @DisplayName("handleAttendanceChecked 메서드는")
    inner class HandleAttendanceChecked {
        @Test
        fun `출석 체크 이벤트 수신 시 일일 출석 포인트를 지급한다`() {
            // given
            val memberId = 1L
            val point = Point.create(id = 1L, memberId = memberId)
            val event =
                AttendanceCheckedEvent(
                    memberId = memberId,
                    checkedDate = LocalDate.now(),
                    currentStreak = 1,
                    milestone = null
                )

            every { pointRepository.findWithLockByMemberId(memberId) } returns point
            every { pointHistoryRepository.existsByEventId(any()) } returns false
            every { pointCustomRepository.getGainedPointsToday(memberId, any()) } returns 0L
            every { idGenerator.nextId() } returns 100L
            every { pointHistoryRepository.save(any()) } returns mockk<PointHistory>()

            // when
            sut.handleAttendanceChecked(event)

            // then
            assertThat(point.amount).isEqualTo(10L)
            verify(exactly = 1) { pointHistoryRepository.save(any()) }
        }

        @Test
        fun `7일 연속 출석 시 일일 포인트와 보너스 포인트를 지급한다`() {
            // given
            val memberId = 1L
            val point = Point.create(id = 1L, memberId = memberId)
            val event =
                AttendanceCheckedEvent(
                    memberId = memberId,
                    checkedDate = LocalDate.now(),
                    currentStreak = 7,
                    milestone = StreakMileStone.ONE_WEEK
                )
            val historySlots = mutableListOf<PointHistory>()

            every { pointRepository.findWithLockByMemberId(memberId) } returns point
            every { pointHistoryRepository.existsByEventId(any()) } returns false
            every { pointCustomRepository.getGainedPointsToday(memberId, any()) } returns 0L
            every { idGenerator.nextId() } returnsMany listOf(100L, 101L)
            every { pointHistoryRepository.save(capture(historySlots)) } returns mockk<PointHistory>()

            // when
            sut.handleAttendanceChecked(event)

            // then
            assertThat(point.amount).isEqualTo(60L) // 10 + 50
            verify(exactly = 2) { pointHistoryRepository.save(any()) }

            val reasons = historySlots.map { it.reason }
            assertThat(reasons).containsExactly(
                PointReason.ATTENDANCE,
                PointReason.ATTENDANCE_STREAK_FOR_7_DAYS
            )
        }

        @Test
        fun `14일 연속 출석 시 일일 포인트와 보너스 포인트를 지급한다`() {
            // given
            val memberId = 1L
            val point = Point.create(id = 1L, memberId = memberId)
            val event =
                AttendanceCheckedEvent(
                    memberId = memberId,
                    checkedDate = LocalDate.now(),
                    currentStreak = 14,
                    milestone = StreakMileStone.TWO_WEEKS
                )

            every { pointRepository.findWithLockByMemberId(memberId) } returns point
            every { pointHistoryRepository.existsByEventId(any()) } returns false
            // 첫 번째 지급 후 10P, 두 번째 지급 시 이미 10P 받은 상태
            every { pointCustomRepository.getGainedPointsToday(memberId, any()) } returnsMany listOf(0L, 10L)
            every { idGenerator.nextId() } returnsMany listOf(100L, 101L)
            every { pointHistoryRepository.save(any()) } returns mockk<PointHistory>()

            // when
            sut.handleAttendanceChecked(event)

            // then
            assertThat(point.amount).isEqualTo(100L) // 10P + 90P (100P 제한으로 100P 중 90P만)
        }

        @Test
        fun `Point가 없는 경우 예외를 던진다`() {
            // given
            val memberId = 1L
            val event =
                AttendanceCheckedEvent(
                    memberId = memberId,
                    checkedDate = LocalDate.now(),
                    currentStreak = 1,
                    milestone = null
                )

            every { pointRepository.findWithLockByMemberId(memberId) } returns null

            // when & then
            assertThatThrownBy { sut.handleAttendanceChecked(event) }
                .isInstanceOf(GeneralException::class.java)
        }

        @Test
        fun `중복 이벤트인 경우 포인트를 지급하지 않는다`() {
            // given
            val memberId = 1L
            val point = Point.create(id = 1L, memberId = memberId)
            val event =
                AttendanceCheckedEvent(
                    memberId = memberId,
                    checkedDate = LocalDate.now(),
                    currentStreak = 1,
                    milestone = null
                )

            every { pointRepository.findWithLockByMemberId(memberId) } returns point
            every { pointHistoryRepository.existsByEventId(any()) } returns true

            // when
            sut.handleAttendanceChecked(event)

            // then
            assertThat(point.amount).isEqualTo(0L)
            verify(exactly = 0) { pointHistoryRepository.save(any()) }
        }

        @Test
        fun `마일스톤 보너스가 이미 지급된 경우 일일 포인트만 지급한다`() {
            // given
            val memberId = 1L
            val point = Point.create(id = 1L, memberId = memberId)
            val event =
                AttendanceCheckedEvent(
                    memberId = memberId,
                    checkedDate = LocalDate.now(),
                    currentStreak = 7,
                    milestone = StreakMileStone.ONE_WEEK
                )

            every { pointRepository.findWithLockByMemberId(memberId) } returns point
            every { pointHistoryRepository.existsByEventId(event.eventId) } returns false
            every {
                pointHistoryRepository.existsByEventId("${event.eventId}-one_week")
            } returns true
            every { pointCustomRepository.getGainedPointsToday(memberId, any()) } returns 0L
            every { idGenerator.nextId() } returns 100L
            every { pointHistoryRepository.save(any()) } returns mockk<PointHistory>()

            // when
            sut.handleAttendanceChecked(event)

            // then
            assertThat(point.amount).isEqualTo(10L) // 일일 포인트만
            verify(exactly = 1) { pointHistoryRepository.save(any()) }
        }
    }

    @Nested
    @DisplayName("하루 최대 100P 제한은")
    inner class DailyMaxPointsLimit {
        @Test
        fun `오늘 이미 100P를 받은 경우 포인트를 지급하지 않는다`() {
            // given
            val memberId = 1L
            val point = Point.create(id = 1L, memberId = memberId)
            val event =
                AttendanceCheckedEvent(
                    memberId = memberId,
                    checkedDate = LocalDate.now(),
                    currentStreak = 1,
                    milestone = null
                )

            every { pointRepository.findWithLockByMemberId(memberId) } returns point
            every { pointHistoryRepository.existsByEventId(any()) } returns false
            every { pointCustomRepository.getGainedPointsToday(memberId, any()) } returns 100L

            // when
            sut.handleAttendanceChecked(event)

            // then
            assertThat(point.amount).isEqualTo(0L)
            verify(exactly = 0) { pointHistoryRepository.save(any()) }
        }

        @Test
        fun `지급하면 100P를 초과하는 경우 남은 만큼만 지급한다`() {
            // given
            val memberId = 1L
            val point = Point.create(id = 1L, memberId = memberId)
            val event =
                AttendanceCheckedEvent(
                    memberId = memberId,
                    checkedDate = LocalDate.now(),
                    currentStreak = 1,
                    milestone = null
                )
            val historySlot = slot<PointHistory>()

            every { pointRepository.findWithLockByMemberId(memberId) } returns point
            every { pointHistoryRepository.existsByEventId(any()) } returns false
            every { pointCustomRepository.getGainedPointsToday(memberId, any()) } returns 95L
            every { idGenerator.nextId() } returns 100L
            every { pointHistoryRepository.save(capture(historySlot)) } returns mockk<PointHistory>()

            // when
            sut.handleAttendanceChecked(event)

            // then
            assertThat(point.amount).isEqualTo(5L) // 100 - 95 = 5P만 지급
            assertThat(historySlot.captured.points).isEqualTo(5L)
            verify(exactly = 1) { pointHistoryRepository.save(any()) }
        }

        @Test
        fun `오늘 90P를 받은 상태에서 10P 지급 요청 시 10P 전액 지급한다`() {
            // given
            val memberId = 1L
            val point = Point.create(id = 1L, memberId = memberId)
            val event =
                AttendanceCheckedEvent(
                    memberId = memberId,
                    checkedDate = LocalDate.now(),
                    currentStreak = 1,
                    milestone = null
                )
            val historySlot = slot<PointHistory>()

            every { pointRepository.findWithLockByMemberId(memberId) } returns point
            every { pointHistoryRepository.existsByEventId(any()) } returns false
            every { pointCustomRepository.getGainedPointsToday(memberId, any()) } returns 90L
            every { idGenerator.nextId() } returns 100L
            every { pointHistoryRepository.save(capture(historySlot)) } returns mockk<PointHistory>()

            // when
            sut.handleAttendanceChecked(event)

            // then
            assertThat(point.amount).isEqualTo(10L)
            assertThat(historySlot.captured.points).isEqualTo(10L)
        }
    }
}
