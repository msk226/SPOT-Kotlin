package kr.spot.core.member.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.spot.core.global.domain.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "preferred_region")
@SQLDelete(sql = "UPDATE preferred_region SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class PreferredRegion private constructor(
    @Id
    val id: Long,
    val memberId: Long,
    val regionCode: String
) : BaseEntity() {
    companion object {
        fun of(
            id: Long,
            memberId: Long,
            regionCode: String
        ): PreferredRegion = PreferredRegion(id, memberId, regionCode)
    }
}
