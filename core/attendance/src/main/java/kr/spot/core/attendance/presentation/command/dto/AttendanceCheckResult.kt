package kr.spot.core.attendance.presentation.command.dto

import kr.spot.common.event.contract.StreakMileStone

data class AttendanceCheckResult(
    val currentStreak: Int,
    val maxStreak: Int,
    val mileStone: StreakMileStone?
)
