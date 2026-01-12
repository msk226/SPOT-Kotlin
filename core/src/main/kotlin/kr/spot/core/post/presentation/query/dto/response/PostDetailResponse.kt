package kr.spot.core.post.presentation.query.dto.response

import kr.spot.core.post.domain.enums.PostType
import java.time.LocalDateTime

data class PostDetailResponse(
    val postId: Long,
    val title: String,
    val content: String,
    val imageUrl: String?,
    val postType: PostType,
    val isLiked: Boolean,
    val isOwner: Boolean,
    val writer: WriterInfoResponse,
    val stats: PostStatsResponse,
    val createdAt: LocalDateTime,
    val comments: List<CommentResponse>,
    val commentCount: Int
) {
    data class CommentResponse(
        val commentId: Long,
        val content: String,
        val writer: WriterInfoResponse,
        val createdAt: LocalDateTime
    ) {
        companion object {
            fun of(
                commentId: Long,
                content: String,
                writer: WriterInfoResponse,
                createdAt: LocalDateTime
            ) = CommentResponse(commentId, content, writer, createdAt)
        }
    }

    data class WriterInfoResponse(
        val writerId: Long,
        val nickname: String,
        val profileImageUrl: String?
    ) {
        companion object {
            fun of(
                writerId: Long,
                nickname: String,
                profileImageUrl: String?
            ) = WriterInfoResponse(writerId, nickname, profileImageUrl)
        }
    }

    data class PostStatsResponse(
        val likeCount: Long,
        val commentCount: Long,
        val viewCount: Long
    ) {
        companion object {
            fun of(
                likeCount: Long,
                commentCount: Long,
                viewCount: Long
            ) = PostStatsResponse(likeCount, commentCount, viewCount)
        }
    }
}
