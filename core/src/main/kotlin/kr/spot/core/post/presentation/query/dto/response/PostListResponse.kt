package kr.spot.core.post.presentation.query.dto.response

import kr.spot.core.post.domain.enums.PostType
import java.time.LocalDateTime

data class PostListResponse(
    val posts: List<PostList>,
    val hasNext: Boolean,
    val nextCursor: Long?
) {
    data class PostList(
        val postId: Long,
        val title: String,
        val content: String,
        val postType: PostType,
        val stats: PostStatsResponse,
        val createdAt: LocalDateTime,
        val isLiked: Boolean?
    ) {
        companion object {
            fun of(
                postId: Long,
                title: String,
                content: String,
                postType: PostType,
                stats: PostStatsResponse,
                createdAt: LocalDateTime,
                isLiked: Boolean?
            ) = PostList(postId, title, content, postType, stats, createdAt, isLiked)
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
