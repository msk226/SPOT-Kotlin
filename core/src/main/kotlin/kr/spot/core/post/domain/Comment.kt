package kr.spot.core.post.domain

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.spot.core.global.domain.BaseEntity
import kr.spot.core.post.domain.vo.WriterInfo
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "comment")
@SQLDelete(sql = "UPDATE comment SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class Comment private constructor(
    @Id
    val id: Long,
    val postId: Long,
    @Embedded
    val writerInfo: WriterInfo,
    content: String
) : BaseEntity() {
    var content: String = content
        private set

    fun update(content: String) {
        this.content = content
    }

    fun isOwner(memberId: Long): Boolean = writerInfo.writerId == memberId

    companion object {
        fun of(
            id: Long,
            postId: Long,
            writerInfo: WriterInfo,
            content: String
        ): Comment =
            Comment(
                id,
                postId,
                writerInfo,
                content
            )
    }
}
