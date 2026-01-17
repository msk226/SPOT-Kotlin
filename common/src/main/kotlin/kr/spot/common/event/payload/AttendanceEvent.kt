package kr.spot.common.event.payload

import kr.spot.common.event.contract.StreakMileStone
import java.time.LocalDate

data class AttendanceCheckedEvent (
    val memberId: Long,
    val checkedDate: LocalDate,
    val currentStreak: Int,
    val milestone: StreakMileStone?
)
