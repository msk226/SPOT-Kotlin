package kr.spot.core.attendance.application.event

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import kr.spot.common.event.payload.MemberCreatedEvent
import kr.spot.common.id.IdGenerator
import kr.spot.core.attendance.domain.AttendanceStreak
import kr.spot.core.attendance.infrastructure.AttendanceStreakRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@DisplayName("AttendanceApplicationEventListener")
class AttendanceApplicationEventListenerTest {

    @MockK
    private lateinit var idGenerator: IdGenerator

    @MockK
    private lateinit var attendanceStreakRepository: AttendanceStreakRepository

    @InjectMockKs
    private lateinit var sut: AttendanceApplicationEventListener

    @Nested
    @DisplayName("registerAttendanceStreak 메서드는")
    inner class RegisterAttendanceStreak {

        @Test
        fun `MemberCreatedEvent 수신 시 AttendanceStreak을 생성한다`() {
            // given
            val memberId = 1L
            val event = MemberCreatedEvent(memberId = memberId)
            val streakSlot = slot<AttendanceStreak>()

            every { idGenerator.nextId() } returns 100L
            every { attendanceStreakRepository.save(capture(streakSlot)) } answers { streakSlot.captured }

            // when
            sut.registerAttendanceStreak(event)

            // then
            verify(exactly = 1) { attendanceStreakRepository.save(any()) }
            assertThat(streakSlot.captured.memberId).isEqualTo(memberId)
            assertThat(streakSlot.captured.currentStreak).isEqualTo(0)
            assertThat(streakSlot.captured.maxStreak).isEqualTo(0)
            assertThat(streakSlot.captured.lastCheckDate).isNull()
        }

        @Test
        fun `생성된 AttendanceStreak의 ID는 IdGenerator에서 생성된다`() {
            // given
            val memberId = 1L
            val generatedId = 999L
            val event = MemberCreatedEvent(memberId = memberId)
            val streakSlot = slot<AttendanceStreak>()

            every { idGenerator.nextId() } returns generatedId
            every { attendanceStreakRepository.save(capture(streakSlot)) } answers { streakSlot.captured }

            // when
            sut.registerAttendanceStreak(event)

            // then
            assertThat(streakSlot.captured.id).isEqualTo(generatedId)
        }
    }
}
