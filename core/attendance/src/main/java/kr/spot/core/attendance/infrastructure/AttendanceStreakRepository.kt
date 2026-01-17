package kr.spot.core.attendance.infrastructure

import kr.spot.core.attendance.domain.AttendanceStreak
import org.springframework.data.jpa.repository.JpaRepository

interface AttendanceStreakRepository : JpaRepository<AttendanceStreak, Long> {
    fun findByMemberId(memberId: Long): AttendanceStreak?
}
