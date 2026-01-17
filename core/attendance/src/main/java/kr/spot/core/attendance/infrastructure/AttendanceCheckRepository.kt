package kr.spot.core.attendance.infrastructure

import kr.spot.core.attendance.domain.AttendanceCheck
import org.springframework.data.jpa.repository.JpaRepository

interface AttendanceCheckRepository : JpaRepository<AttendanceCheck, Long> {
    fun existsByMemberIdAndCheckDate(
        memberId: Long,
        checkDate: java.time.LocalDate
    ): Boolean
}
