package kr.spot.study.schedule.presentation.command.dto

data class CreateScheduleResponse(
    val scheduleId: Long
) {
    companion object {
        fun from(scheduleId: Long): CreateScheduleResponse = CreateScheduleResponse(scheduleId)
    }
}
