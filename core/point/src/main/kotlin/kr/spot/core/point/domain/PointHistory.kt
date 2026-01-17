package kr.spot.core.point.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.spot.common.domain.BaseEntity
import kr.spot.common.event.contract.PointReason
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "point_history")
@SQLDelete(sql = "UPDATE point_history SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class PointHistory private constructor(
    @Id
    val id: Long,
    @Column(name = "event_id", unique = true, nullable = false, length = 36)
    val eventId: String,
    val memberId: Long,
    val points: Long,
    @Enumerated(EnumType.STRING)
    val reason: PointReason,
    val referenceId: Long?,
    val grantedAt: LocalDateTime
) : BaseEntity() {
    companion object {
        fun of(
            id: Long,
            eventId: String,
            memberId: Long,
            points: Long,
            reason: PointReason,
            referenceId: Long?,
            grantedAt: LocalDateTime
        ): PointHistory =
            PointHistory(
                id = id,
                eventId = eventId,
                memberId = memberId,
                points = points,
                reason = reason,
                referenceId = referenceId,
                grantedAt = grantedAt
            )
    }
}
