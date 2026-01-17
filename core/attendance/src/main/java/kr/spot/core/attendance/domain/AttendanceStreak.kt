package kr.spot.core.attendance.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.spot.common.domain.BaseEntity
import kr.spot.common.event.contract.StreakMileStone
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDate

@Entity
@Table(name = "attendance_streak")
@SQLDelete(sql = "UPDATE attendance_streak SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class AttendanceStreak private constructor(
    @Id
    val id: Long,
    @Column(name = "member_id", nullable = false)
    val memberId: Long,
    currentStreak: Int,
    maxStreak: Int,
    lastCheckDate: LocalDate?
) : BaseEntity() {
    @Column(name = "current_streak", nullable = false)
    var currentStreak: Int = currentStreak
        private set

    @Column(name = "max_streak", nullable = false)
    var maxStreak: Int = maxStreak
        private set

    @Column(name = "last_check_date")
    var lastCheckDate: LocalDate? = lastCheckDate
        private set

    fun recordAttendance(checkDate: LocalDate): StreakMileStone? {
        val previousStreak = currentStreak

        when {
            lastCheckDate == null -> {
                currentStreak = 1
            }
            lastCheckDate == checkDate.minusDays(1) -> {
                currentStreak += 1
            }
            lastCheckDate == checkDate -> {
                return null
            } else -> {
                currentStreak = 1
            }
        }

        lastCheckDate = checkDate
        maxStreak = maxOf(maxStreak, currentStreak)

        return checkMilestone(previousStreak, currentStreak)
    }

    private fun checkMilestone(
        previous: Int,
        current: Int
    ): StreakMileStone? =
        when {
            previous < 7 && current >= 7 -> StreakMileStone.ONE_WEEK
            previous < 14 && current >= 14 -> StreakMileStone.TWO_WEEKS
            else -> null
        }

    companion object {
        fun create(
            id: Long,
            memberId: Long
        ): AttendanceStreak =
            AttendanceStreak(
                id = id,
                memberId = memberId,
                currentStreak = 0,
                maxStreak = 0,
                lastCheckDate = null
            )
    }
}
