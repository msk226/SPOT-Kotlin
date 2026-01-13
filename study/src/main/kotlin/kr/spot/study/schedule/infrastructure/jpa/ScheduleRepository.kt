package kr.spot.study.schedule.infrastructure.jpa

import kr.spot.study.schedule.domain.Schedule
import org.springframework.data.jpa.repository.JpaRepository

interface ScheduleRepository : JpaRepository<Schedule, Long>
