package kr.spot.study.schedule.presentation.query.dto

import java.time.LocalDateTime

data class GetScheduleListResponse(
    val schedules: List<ScheduleResponse>,
    val totalCount: Long
) {
    companion object {
        fun from(schedules: List<ScheduleResponse>): GetScheduleListResponse =
            GetScheduleListResponse(schedules, schedules.size.toLong())
    }

    data class ScheduleResponse(
        val scheduleId: Long,
        val title: String,
        val startAt: LocalDateTime?,
        val endAt: LocalDateTime?,
        val isNow: Boolean
    ) {
        companion object {
            fun from(
                scheduleId: Long,
                title: String,
                startAt: LocalDateTime?,
                endAt: LocalDateTime?,
                isNow: Boolean
            ): ScheduleResponse = ScheduleResponse(scheduleId, title, startAt, endAt, isNow)
        }
    }
}
