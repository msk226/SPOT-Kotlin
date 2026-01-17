package kr.spot.core.attendance.domain

import kr.spot.common.event.contract.StreakMileStone
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

@DisplayName("AttendanceStreak 도메인")
class AttendanceStreakTest {

    @Nested
    @DisplayName("create 메서드는")
    inner class Create {

        @Test
        fun `초기 상태의 AttendanceStreak을 생성한다`() {
            // when
            val streak = AttendanceStreak.create(id = 1L, memberId = 100L)

            // then
            assertThat(streak.id).isEqualTo(1L)
            assertThat(streak.memberId).isEqualTo(100L)
            assertThat(streak.currentStreak).isEqualTo(0)
            assertThat(streak.maxStreak).isEqualTo(0)
            assertThat(streak.lastCheckDate).isNull()
        }
    }

    @Nested
    @DisplayName("recordAttendance 메서드는")
    inner class RecordAttendance {

        @Nested
        @DisplayName("첫 출석일 때")
        inner class WhenFirstAttendance {

            @Test
            fun `currentStreak을 1로 설정한다`() {
                // given
                val streak = AttendanceStreak.create(id = 1L, memberId = 1L)
                val today = LocalDate.of(2024, 1, 1)

                // when
                streak.recordAttendance(today)

                // then
                assertThat(streak.currentStreak).isEqualTo(1)
                assertThat(streak.maxStreak).isEqualTo(1)
                assertThat(streak.lastCheckDate).isEqualTo(today)
            }
        }

        @Nested
        @DisplayName("연속 출석일 때")
        inner class WhenConsecutiveAttendance {

            @Test
            fun `currentStreak을 1 증가시킨다`() {
                // given
                val streak = AttendanceStreak.create(id = 1L, memberId = 1L)
                val day1 = LocalDate.of(2024, 1, 1)
                val day2 = LocalDate.of(2024, 1, 2)

                streak.recordAttendance(day1)

                // when
                streak.recordAttendance(day2)

                // then
                assertThat(streak.currentStreak).isEqualTo(2)
                assertThat(streak.maxStreak).isEqualTo(2)
            }

            @Test
            fun `5일 연속 출석 시 currentStreak이 5가 된다`() {
                // given
                val streak = AttendanceStreak.create(id = 1L, memberId = 1L)
                val startDate = LocalDate.of(2024, 1, 1)

                // when
                repeat(5) { day ->
                    streak.recordAttendance(startDate.plusDays(day.toLong()))
                }

                // then
                assertThat(streak.currentStreak).isEqualTo(5)
                assertThat(streak.maxStreak).isEqualTo(5)
            }
        }

        @Nested
        @DisplayName("같은 날 중복 출석일 때")
        inner class WhenSameDayAttendance {

            @Test
            fun `null을 반환하고 streak을 변경하지 않는다`() {
                // given
                val streak = AttendanceStreak.create(id = 1L, memberId = 1L)
                val today = LocalDate.of(2024, 1, 1)
                streak.recordAttendance(today)

                // when
                val result = streak.recordAttendance(today)

                // then
                assertThat(result).isNull()
                assertThat(streak.currentStreak).isEqualTo(1)
            }
        }

        @Nested
        @DisplayName("연속 출석이 끊겼을 때")
        inner class WhenStreakBroken {

            @Test
            fun `currentStreak을 1로 리셋한다`() {
                // given
                val streak = AttendanceStreak.create(id = 1L, memberId = 1L)
                streak.recordAttendance(LocalDate.of(2024, 1, 1))
                streak.recordAttendance(LocalDate.of(2024, 1, 2))
                streak.recordAttendance(LocalDate.of(2024, 1, 3))

                // when - 하루 건너뛰고 출석
                streak.recordAttendance(LocalDate.of(2024, 1, 5))

                // then
                assertThat(streak.currentStreak).isEqualTo(1)
            }

            @Test
            fun `maxStreak은 유지한다`() {
                // given
                val streak = AttendanceStreak.create(id = 1L, memberId = 1L)
                streak.recordAttendance(LocalDate.of(2024, 1, 1))
                streak.recordAttendance(LocalDate.of(2024, 1, 2))
                streak.recordAttendance(LocalDate.of(2024, 1, 3))

                // when - 하루 건너뛰고 출석
                streak.recordAttendance(LocalDate.of(2024, 1, 5))

                // then
                assertThat(streak.maxStreak).isEqualTo(3)
            }
        }

        @Nested
        @DisplayName("마일스톤 달성 시")
        inner class WhenMilestoneAchieved {

            @Test
            fun `7일 연속 출석 시 ONE_WEEK을 반환한다`() {
                // given
                val streak = AttendanceStreak.create(id = 1L, memberId = 1L)
                val startDate = LocalDate.of(2024, 1, 1)

                // 6일 출석
                repeat(6) { day ->
                    streak.recordAttendance(startDate.plusDays(day.toLong()))
                }

                // when - 7일째 출석
                val milestone = streak.recordAttendance(startDate.plusDays(6))

                // then
                assertThat(milestone).isEqualTo(StreakMileStone.ONE_WEEK)
                assertThat(streak.currentStreak).isEqualTo(7)
            }

            @Test
            fun `14일 연속 출석 시 TWO_WEEKS을 반환한다`() {
                // given
                val streak = AttendanceStreak.create(id = 1L, memberId = 1L)
                val startDate = LocalDate.of(2024, 1, 1)

                // 13일 출석
                repeat(13) { day ->
                    streak.recordAttendance(startDate.plusDays(day.toLong()))
                }

                // when - 14일째 출석
                val milestone = streak.recordAttendance(startDate.plusDays(13))

                // then
                assertThat(milestone).isEqualTo(StreakMileStone.TWO_WEEKS)
                assertThat(streak.currentStreak).isEqualTo(14)
            }

            @Test
            fun `마일스톤 이후 연속 출석 시 null을 반환한다`() {
                // given
                val streak = AttendanceStreak.create(id = 1L, memberId = 1L)
                val startDate = LocalDate.of(2024, 1, 1)

                // 7일 출석 (ONE_WEEK 달성)
                repeat(7) { day ->
                    streak.recordAttendance(startDate.plusDays(day.toLong()))
                }

                // when - 8일째 출석
                val milestone = streak.recordAttendance(startDate.plusDays(7))

                // then
                assertThat(milestone).isNull()
                assertThat(streak.currentStreak).isEqualTo(8)
            }
        }
    }
}
