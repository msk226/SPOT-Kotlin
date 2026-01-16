package kr.spot.core.point.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.spot.common.domain.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "point")
@SQLDelete(sql = "UPDATE point SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class Point private constructor(
    @Id
    val id: Long,
    val memberId: Long,
    amount: Long
) : BaseEntity() {
    var amount: Long = amount
        private set

    companion object {
        fun create(
            id: Long,
            memberId: Long
        ): Point =
            Point(
                id = id,
                memberId = memberId,
                amount = 0L
            )
    }
}
