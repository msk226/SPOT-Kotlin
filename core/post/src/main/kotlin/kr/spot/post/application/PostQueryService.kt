package kr.spot.post.application

import kr.spot.post.application.mapper.PostMapper
import kr.spot.post.domain.enums.PostType
import kr.spot.post.infrastructure.jpa.CommentRepository
import kr.spot.post.infrastructure.jpa.PostLikeRepository
import kr.spot.post.infrastructure.jpa.PostRepository
import kr.spot.post.infrastructure.jpa.findByIdOrThrow
import kr.spot.post.presentation.query.dto.response.PostDetailResponse
import kr.spot.post.presentation.query.dto.response.PostListResponse
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PostQueryService(
    private val postRepository: kr.spot.post.infrastructure.jpa.PostRepository,
    private val commentRepository: kr.spot.post.infrastructure.jpa.CommentRepository,
    private val postLikeRepository: kr.spot.post.infrastructure.jpa.PostLikeRepository,
    private val postMapper: kr.spot.post.application.mapper.PostMapper
) {
    /**
     * 게시글 상세 조회
     */
    fun getPostDetail(
        postId: Long,
        memberId: Long?
    ): kr.spot.post.presentation.query.dto.response.PostDetailResponse {
        val post = postRepository.findByIdOrThrow(postId)
        val comments = commentRepository.getCommentsByPostId(postId)
        val isLiked = memberId?.let { postLikeRepository.existsByPostIdAndMemberId(postId, it) } ?: false
        val isOwner = memberId?.let { post.isOwner(it) } ?: false

        return postMapper.toDetailResponse(post, comments, isLiked, isOwner)
    }

    /**
     * 게시글 목록 조회 (커서 기반 페이징)
     */
    fun getPostList(
        cursor: Long?,
        size: Int,
        postType: kr.spot.post.domain.enums.PostType?,
        memberId: Long?
    ): kr.spot.post.presentation.query.dto.response.PostListResponse {
        val pageable = PageRequest.of(0, size + 1)
        val posts = postRepository.findPostsWithCursor(cursor, postType, pageable)

        val hasNext = posts.size > size
        val resultPosts = if (hasNext) posts.dropLast(1) else posts
        val nextCursor = if (hasNext) resultPosts.lastOrNull()?.id else null

        val postIds = resultPosts.map { it.id }
        val likedPostIds = memberId?.let { postLikeRepository.findLikedPostIds(it, postIds) }?.toSet() ?: emptySet()

        return postMapper.toListResponse(resultPosts, likedPostIds, memberId, hasNext, nextCursor)
    }
}
