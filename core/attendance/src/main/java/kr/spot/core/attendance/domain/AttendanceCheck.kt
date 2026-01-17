package kr.spot.core.attendance.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import kr.spot.common.domain.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDate

@Entity
@Table(
    name = "attendance_check",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_member_date",
            columnNames = ["member_id", "check_date"]
        )
    ]
)
@SQLDelete(sql = "UPDATE attendance_check SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class AttendanceCheck private constructor(
    @Id
    val id: Long,
    @Column(name = "member_id", nullable = false)
    val memberId: Long,
    @Column(name = "check_date", nullable = false)
    val checkDate: LocalDate,
    @Column(name = "checked_at", nullable = false)
    val checkedAt: LocalDate
) : BaseEntity() {
    companion object {
        fun of(
            id: Long,
            memberId: Long
        ): AttendanceCheck =
            AttendanceCheck(
                id = id,
                memberId = memberId,
                checkDate = LocalDate.now(),
                checkedAt = LocalDate.now()
            )
    }
}
