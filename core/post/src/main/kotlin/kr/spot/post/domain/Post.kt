package kr.spot.post.domain

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.spot.common.domain.BaseEntity
import kr.spot.post.domain.enums.PostType
import kr.spot.post.domain.vo.PostStats
import kr.spot.post.domain.vo.WriterInfo
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "post")
@SQLDelete(sql = "UPDATE post SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class Post private constructor(
    @Id
    val id: Long,
    @Embedded
    val writerInfo: WriterInfo,
    title: String,
    content: String,
    postType: PostType,
    @Embedded
    val postStats: PostStats
) : BaseEntity() {
    var title: String = title
        private set

    var content: String = content
        private set

    var postType: PostType = postType
        private set

    fun update(
        title: String,
        content: String,
        postType: PostType
    ) {
        this.title = title
        this.content = content
        this.postType = postType
    }

    fun isOwner(memberId: Long): Boolean = writerInfo.writerId == memberId

    companion object {
        fun of(
            id: Long,
            writerInfo: WriterInfo,
            title: String,
            content: String,
            postType: PostType,
            postStats: PostStats
        ): Post = Post(id, writerInfo, title, content, postType, postStats)
    }
}
