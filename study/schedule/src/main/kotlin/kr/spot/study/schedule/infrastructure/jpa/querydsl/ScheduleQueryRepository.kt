package kr.spot.study.schedule.infrastructure.jpa.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.spot.study.schedule.domain.QSchedule
import kr.spot.study.schedule.domain.Schedule
import org.springframework.stereotype.Repository
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth

@Repository
class ScheduleQueryRepository(
    private val query: JPAQueryFactory
) {
    fun findMonthlySchedules(
        studyId: Long,
        date: LocalDate
    ): List<Schedule> {
        val schedule = QSchedule.schedule

        val yearMonth = YearMonth.from(date)
        val startOfMonth = yearMonth.atDay(1).atStartOfDay()
        val endOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX)

        return query
            .selectFrom(schedule)
            .where(
                schedule.studyId.eq(studyId),
                schedule.startAt
                    .between(startOfMonth, endOfMonth)
                    .or(schedule.endAt.between(startOfMonth, endOfMonth))
            ).orderBy(schedule.startAt.asc())
            .fetch()
    }

    fun findWeeklySchedules(
        studyId: Long,
        date: LocalDate
    ): List<Schedule> {
        val schedule = QSchedule.schedule

        val monday = date.with(DayOfWeek.MONDAY)
        val sunday = date.with(DayOfWeek.SUNDAY)
        val startOfWeek = monday.atStartOfDay()
        val endOfWeek = sunday.atTime(LocalTime.MAX)

        return query
            .selectFrom(schedule)
            .where(
                schedule.studyId.eq(studyId),
                schedule.startAt
                    .between(startOfWeek, endOfWeek)
                    .or(schedule.endAt.between(startOfWeek, endOfWeek))
            ).orderBy(schedule.startAt.asc())
            .fetch()
    }

    fun findUpcomingSchedules(
        studyId: Long,
        limit: Int
    ): List<Schedule> {
        val schedule = QSchedule.schedule

        val now = LocalDateTime.now()

        return query
            .selectFrom(schedule)
            .where(
                schedule.studyId.eq(studyId),
                schedule.endAt.goe(now)
            ).orderBy(schedule.startAt.asc())
            .limit(limit.toLong())
            .fetch()
    }
}
