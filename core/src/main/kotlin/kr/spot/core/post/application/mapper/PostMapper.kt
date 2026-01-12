package kr.spot.core.post.application.mapper

import kr.spot.core.post.domain.Comment
import kr.spot.core.post.domain.Post
import kr.spot.core.post.presentation.query.dto.response.PostDetailResponse
import kr.spot.core.post.presentation.query.dto.response.PostDetailResponse.CommentResponse
import kr.spot.core.post.presentation.query.dto.response.PostDetailResponse.PostStatsResponse
import kr.spot.core.post.presentation.query.dto.response.PostDetailResponse.WriterInfoResponse
import kr.spot.core.post.presentation.query.dto.response.PostListResponse
import org.springframework.stereotype.Component

@Component
class PostMapper {
    fun toDetailResponse(
        post: Post,
        comments: List<Comment>,
        isLiked: Boolean,
        isOwner: Boolean
    ): PostDetailResponse =
        PostDetailResponse(
            postId = post.id,
            title = post.title,
            content = post.content,
            imageUrl = null,
            postType = post.postType,
            isLiked = isLiked,
            isOwner = isOwner,
            writer = toWriterInfoResponse(post),
            stats = toPostStatsResponse(post),
            createdAt = post.createdAt!!,
            comments = comments.map { toCommentResponse(it) },
            commentCount = comments.size
        )

    fun toListResponse(
        posts: List<Post>,
        likedPostIds: Set<Long>,
        memberId: Long?,
        hasNext: Boolean,
        nextCursor: Long?
    ): PostListResponse =
        PostListResponse(
            posts =
                posts.map { post ->
                    PostListResponse.PostList.of(
                        postId = post.id,
                        title = post.title,
                        content = post.content,
                        postType = post.postType,
                        stats =
                            PostListResponse.PostStatsResponse.of(
                                likeCount = post.postStats.likes,
                                commentCount = post.postStats.comments,
                                viewCount = post.postStats.views
                            ),
                        createdAt = post.createdAt!!,
                        isLiked = memberId?.let { post.id in likedPostIds }
                    )
                },
            hasNext = hasNext,
            nextCursor = nextCursor
        )

    private fun toWriterInfoResponse(post: Post): WriterInfoResponse =
        WriterInfoResponse.of(
            writerId = post.writerInfo.writerId,
            nickname = post.writerInfo.writerNickname,
            profileImageUrl = post.writerInfo.writerProfileImageUrl
        )

    private fun toPostStatsResponse(post: Post): PostStatsResponse =
        PostStatsResponse.of(
            likeCount = post.postStats.likes,
            commentCount = post.postStats.comments,
            viewCount = post.postStats.views
        )

    private fun toCommentResponse(comment: Comment): CommentResponse =
        CommentResponse.of(
            commentId = comment.id,
            content = comment.content,
            writer =
                WriterInfoResponse.of(
                    writerId = comment.writerInfo.writerId,
                    nickname = comment.writerInfo.writerNickname,
                    profileImageUrl = comment.writerInfo.writerProfileImageUrl
                ),
            createdAt = comment.createdAt!!
        )
}
