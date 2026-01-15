package kr.spot.study.schedule.infrastructure.jpa

import kr.spot.study.schedule.domain.Attendance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface AttendanceRepository : JpaRepository<Attendance, Long> {
    fun findByScheduleIdAndMemberInfoMemberId(
        scheduleId: Long,
        memberId: Long
    ): Optional<Attendance>

    fun findAllByScheduleId(scheduleId: Long): List<Attendance>

    @Modifying
    @Query(
        """
        UPDATE Attendance a
        SET a.memberInfo.memberName = :nickname,
            a.memberInfo.memberProfileImageUrl = :profileImageUrl
        WHERE a.memberInfo.memberId = :memberId
        """
    )
    fun updateMemberInfo(
        memberId: Long,
        nickname: String,
        profileImageUrl: String?
    ): Int
}
