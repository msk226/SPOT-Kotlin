package kr.spot.core.post.domain.association

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import kr.spot.common.domain.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(
    name = "post_like",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_post_like_post_id_member_id",
            columnNames = ["postId", "memberId"]
        )
    ]
)
@SQLDelete(sql = "UPDATE post_like SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class PostLike private constructor(
    @Id
    val id: Long,
    val postId: Long,
    val memberId: Long
) : BaseEntity() {
    companion object {
        fun of(
            id: Long,
            postId: Long,
            memberId: Long
        ): PostLike = PostLike(id, postId, memberId)
    }
}
