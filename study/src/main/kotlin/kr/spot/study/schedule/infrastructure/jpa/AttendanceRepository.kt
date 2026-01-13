package kr.spot.study.schedule.infrastructure.jpa

import kr.spot.study.schedule.domain.Attendance
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface AttendanceRepository : JpaRepository<Attendance, Long> {
    fun findByScheduleIdAndMemberInfoMemberId(
        scheduleId: Long,
        memberId: Long
    ): Optional<Attendance>

    fun findAllByScheduleId(scheduleId: Long): List<Attendance>
}
