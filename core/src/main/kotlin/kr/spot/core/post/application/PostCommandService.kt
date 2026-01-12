package kr.spot.core.post.application

import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.exception.GeneralException
import kr.spot.common.id.IdGenerator
import kr.spot.core.member.application.MemberQueryService
import kr.spot.core.post.domain.Post
import kr.spot.core.post.domain.enums.PostType
import kr.spot.core.post.domain.vo.PostStats
import kr.spot.core.post.domain.vo.WriterInfo
import kr.spot.core.post.infrastructure.jpa.PostLikeRepository
import kr.spot.core.post.infrastructure.jpa.PostRepository
import kr.spot.core.post.infrastructure.jpa.findByIdOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PostCommandService(
    private val idGenerator: IdGenerator,
    private val postRepository: PostRepository,
    private val postLikeRepository: PostLikeRepository,
    private val memberQueryService: MemberQueryService
) {
    /**
     * 게시글 생성
     */
    fun createPost(
        memberId: Long,
        title: String,
        content: String,
        postType: PostType
    ): Long {
        val member = memberQueryService.getMember(memberId)
        val writerInfo =
            WriterInfo.of(
                writerId = member.id,
                writerNickname = member.name,
                writerProfileImageUrl = member.profileImageUrl
            )

        val post =
            Post.of(
                id = idGenerator.nextId(),
                writerInfo = writerInfo,
                title = title,
                content = content,
                postType = postType,
                postStats = PostStats.of(views = 0, likes = 0, comments = 0)
            )

        return postRepository.save(post).id
    }

    /**
     * 게시글 수정
     */
    fun updatePost(
        memberId: Long,
        postId: Long,
        title: String,
        content: String,
        postType: PostType
    ) {
        val post = postRepository.findByIdOrThrow(postId)
        validateOwner(post, memberId)
        post.update(title, content, postType)
    }

    /**
     * 게시글 삭제
     */
    fun deletePost(
        memberId: Long,
        postId: Long
    ) {
        val post = postRepository.findByIdOrThrow(postId)
        validateOwner(post, memberId)
        postRepository.delete(post)
    }

    /**
     * 게시글 좋아요
     */
    fun likePost(
        memberId: Long,
        postId: Long
    ) {
        postLikeRepository.savePostLike(
            id = idGenerator.nextId(),
            postId = postId,
            memberId = memberId
        )
    }

    /**
     * 게시글 좋아요 취소
     */
    fun unlikePost(
        memberId: Long,
        postId: Long
    ) {
        postLikeRepository.hardDelete(postId = postId, memberId = memberId)
    }

    private fun validateOwner(
        post: Post,
        memberId: Long
    ) {
        if (!post.isOwner(memberId)) {
            throw GeneralException(ErrorStatus.FORBIDDEN)
        }
    }
}
