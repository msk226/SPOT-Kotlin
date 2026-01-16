package kr.spot.core.member.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.spot.common.domain.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "preferred_category")
@SQLDelete(sql = "UPDATE preferred_category SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class PreferredCategory private constructor(
    @Id
    val id: Long,
    val memberId: Long,
    val category: String
) : BaseEntity() {
    companion object {
        fun of(
            id: Long,
            memberId: Long,
            category: String
        ): kr.spot.core.member.domain.PreferredCategory =
            _root_ide_package_.kr.spot.core.member.domain
                .PreferredCategory(id, memberId, category)
    }
}
