package kr.spot.study.schedule.application.query

import kr.spot.study.schedule.domain.Schedule
import kr.spot.study.schedule.infrastructure.jpa.querydsl.ScheduleQueryRepository
import kr.spot.study.schedule.presentation.query.dto.GetScheduleListResponse
import kr.spot.study.schedule.presentation.query.dto.GetScheduleListResponse.ScheduleResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class GetScheduleService(
    private val scheduleQueryRepository: ScheduleQueryRepository
) {
    fun getMonthlySchedules(
        studyId: Long,
        year: Int,
        month: Int
    ): GetScheduleListResponse {
        val date = LocalDate.of(year, month, 1)
        val schedules = scheduleQueryRepository.findMonthlySchedules(studyId, date)
        return toResponse(schedules)
    }

    fun getWeeklySchedules(
        studyId: Long,
        date: LocalDate
    ): GetScheduleListResponse {
        val schedules = scheduleQueryRepository.findWeeklySchedules(studyId, date)
        return toResponse(schedules)
    }

    fun getUpcomingSchedules(studyId: Long): GetScheduleListResponse {
        val schedules = scheduleQueryRepository.findUpcomingSchedules(studyId, UPCOMING_LIMIT)
        return toResponse(schedules)
    }

    private fun toResponse(schedules: List<Schedule>): GetScheduleListResponse {
        val now = LocalDateTime.now()

        val responses =
            schedules.map { schedule ->
                ScheduleResponse.from(
                    schedule.id,
                    schedule.title,
                    schedule.startAt,
                    schedule.endAt,
                    schedule.isOngoing(now)
                )
            }

        return GetScheduleListResponse.from(responses)
    }

    companion object {
        private const val UPCOMING_LIMIT = 2
    }
}
