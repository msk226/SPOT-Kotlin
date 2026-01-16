package kr.spot.core.point.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.spot.common.domain.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "point_history")
@SQLDelete(sql = "UPDATE point_history SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class PointHistory private constructor(
    @Id
    val id: Long,
) : BaseEntity()
