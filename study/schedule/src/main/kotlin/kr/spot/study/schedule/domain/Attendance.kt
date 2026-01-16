package kr.spot.study.schedule.domain

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import kr.spot.common.domain.BaseEntity
import kr.spot.study.schedule.domain.enums.AttendanceStatus
import kr.spot.study.schedule.domain.vo.MemberInfo
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@SQLDelete(sql = "UPDATE attendance SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class Attendance private constructor(
    @Id
    val id: Long,
    val scheduleId: Long,
    @Embedded
    val memberInfo: MemberInfo,
    attendanceStatus: AttendanceStatus,
    attendedAt: LocalDateTime?
) : BaseEntity() {
    @Enumerated(EnumType.STRING)
    var attendanceStatus: AttendanceStatus = attendanceStatus
        private set

    var attendedAt: LocalDateTime? = attendedAt
        private set

    fun markAttendance(status: AttendanceStatus) {
        this.attendanceStatus = status
        this.attendedAt = LocalDateTime.now()
    }

    fun markAbsentIfUndecided() {
        if (this.attendanceStatus == AttendanceStatus.UNDECIDED) {
            this.attendanceStatus = AttendanceStatus.ABSENT
        }
    }

    companion object {
        fun of(
            id: Long,
            scheduleId: Long,
            memberInfo: MemberInfo
        ): Attendance =
            Attendance(
                id = id,
                scheduleId = scheduleId,
                memberInfo = memberInfo,
                attendanceStatus = AttendanceStatus.UNDECIDED,
                attendedAt = null
            )

        fun createPending(
            id: Long,
            scheduleId: Long,
            memberInfo: MemberInfo
        ): Attendance =
            Attendance(
                id = id,
                scheduleId = scheduleId,
                memberInfo = memberInfo,
                attendanceStatus = AttendanceStatus.UNDECIDED,
                attendedAt = null
            )
    }
}
