package kr.spot.core.attendance.application.command

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kr.spot.common.api.exception.GeneralException
import kr.spot.common.event.contract.StreakMileStone
import kr.spot.common.event.payload.AttendanceCheckedEvent
import kr.spot.common.id.IdGenerator
import kr.spot.core.attendance.domain.AttendanceCheck
import kr.spot.core.attendance.domain.AttendanceStreak
import kr.spot.core.attendance.infrastructure.AttendanceCheckRepository
import kr.spot.core.attendance.infrastructure.AttendanceStreakRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
@DisplayName("AttendanceCheckCommandService")
class AttendanceCheckCommandServiceTest {
    @MockK
    private lateinit var idGenerator: IdGenerator

    @MockK
    private lateinit var attendanceCheckRepository: AttendanceCheckRepository

    @MockK
    private lateinit var attendanceStreakRepository: AttendanceStreakRepository

    @MockK
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var sut: AttendanceCheckCommandService

    @Nested
    @DisplayName("checkIn 메서드는")
    inner class CheckIn {
        @Test
        fun `출석 체크 성공 시 AttendanceCheckResult를 반환한다`() {
            // given
            val memberId = 1L
            val streak = AttendanceStreak.create(id = 1L, memberId = memberId)

            every { idGenerator.nextId() } returns 100L
            every {
                attendanceCheckRepository.existsByMemberIdAndCheckDate(memberId, any())
            } returns false
            every { attendanceCheckRepository.save(any()) } returns mockk<AttendanceCheck>()
            every { attendanceStreakRepository.findByMemberId(memberId) } returns streak
            every { applicationEventPublisher.publishEvent(any<AttendanceCheckedEvent>()) } just Runs

            // when
            val result = sut.checkIn(memberId)

            // then
            assertThat(result.currentStreak).isEqualTo(1)
            assertThat(result.maxStreak).isEqualTo(1)
            assertThat(result.mileStone).isNull()
        }

        @Test
        fun `출석 체크 성공 시 AttendanceCheckedEvent를 발행한다`() {
            // given
            val memberId = 1L
            val streak = AttendanceStreak.create(id = 1L, memberId = memberId)
            val eventSlot = slot<AttendanceCheckedEvent>()

            every { idGenerator.nextId() } returns 100L
            every {
                attendanceCheckRepository.existsByMemberIdAndCheckDate(memberId, any())
            } returns false
            every { attendanceCheckRepository.save(any()) } returns mockk<AttendanceCheck>()
            every { attendanceStreakRepository.findByMemberId(memberId) } returns streak
            every { applicationEventPublisher.publishEvent(capture(eventSlot)) } just Runs

            // when
            sut.checkIn(memberId)

            // then
            verify(exactly = 1) { applicationEventPublisher.publishEvent(any<AttendanceCheckedEvent>()) }
            assertThat(eventSlot.captured.memberId).isEqualTo(memberId)
            assertThat(eventSlot.captured.currentStreak).isEqualTo(1)
        }

        @Test
        fun `7일 연속 출석 시 ONE_WEEK 마일스톤과 함께 이벤트를 발행한다`() {
            // given
            val memberId = 1L
            val streak = AttendanceStreak.create(id = 1L, memberId = memberId)
            val startDate = LocalDate.now().minusDays(6)

            // 6일 연속 출석 상태로 만들기
            repeat(6) { day ->
                streak.recordAttendance(startDate.plusDays(day.toLong()))
            }

            val eventSlot = slot<AttendanceCheckedEvent>()

            every { idGenerator.nextId() } returns 100L
            every {
                attendanceCheckRepository.existsByMemberIdAndCheckDate(memberId, any())
            } returns false
            every { attendanceCheckRepository.save(any()) } returns mockk<AttendanceCheck>()
            every { attendanceStreakRepository.findByMemberId(memberId) } returns streak
            every { applicationEventPublisher.publishEvent(capture(eventSlot)) } just Runs

            // when
            val result = sut.checkIn(memberId)

            // then
            assertThat(result.mileStone).isEqualTo(StreakMileStone.ONE_WEEK)
            assertThat(eventSlot.captured.milestone).isEqualTo(StreakMileStone.ONE_WEEK)
        }

        @Test
        fun `이미 출석한 경우 예외를 던진다`() {
            // given
            val memberId = 1L

            every {
                attendanceCheckRepository.existsByMemberIdAndCheckDate(memberId, any())
            } returns true

            // when & then
            assertThatThrownBy { sut.checkIn(memberId) }
                .isInstanceOf(GeneralException::class.java)
        }

        @Test
        fun `AttendanceStreak이 없는 경우 예외를 던진다`() {
            // given
            val memberId = 1L

            every { idGenerator.nextId() } returns 100L
            every {
                attendanceCheckRepository.existsByMemberIdAndCheckDate(memberId, any())
            } returns false
            every { attendanceCheckRepository.save(any()) } returns mockk<AttendanceCheck>()
            every { attendanceStreakRepository.findByMemberId(memberId) } returns null

            // when & then
            assertThatThrownBy { sut.checkIn(memberId) }
                .isInstanceOf(GeneralException::class.java)
        }

        @Test
        fun `출석 체크 시 AttendanceCheck을 저장한다`() {
            // given
            val memberId = 1L
            val streak = AttendanceStreak.create(id = 1L, memberId = memberId)
            val checkSlot = slot<AttendanceCheck>()

            every { idGenerator.nextId() } returns 100L
            every {
                attendanceCheckRepository.existsByMemberIdAndCheckDate(memberId, any())
            } returns false
            every { attendanceCheckRepository.save(capture(checkSlot)) } returns mockk<AttendanceCheck>()
            every { attendanceStreakRepository.findByMemberId(memberId) } returns streak
            every { applicationEventPublisher.publishEvent(any<AttendanceCheckedEvent>()) } just Runs

            // when
            sut.checkIn(memberId)

            // then
            verify(exactly = 1) { attendanceCheckRepository.save(any()) }
            assertThat(checkSlot.captured.memberId).isEqualTo(memberId)
            assertThat(checkSlot.captured.checkDate).isEqualTo(LocalDate.now())
        }
    }
}
