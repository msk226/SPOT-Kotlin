package kr.spot.core.post.application

import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.exception.GeneralException
import kr.spot.common.id.IdGenerator
import kr.spot.core.member.application.MemberQueryService
import kr.spot.core.post.domain.Comment
import kr.spot.core.post.domain.vo.WriterInfo
import kr.spot.core.post.infrastructure.jpa.CommentRepository
import kr.spot.core.post.infrastructure.jpa.getByIdOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CommentCommandService(
    private val idGenerator: IdGenerator,
    private val commentRepository: CommentRepository,
    private val memberQueryService: MemberQueryService
) {
    /**
     * 댓글 생성
     */
    fun createComment(
        memberId: Long,
        postId: Long,
        content: String
    ): Long {
        val member = memberQueryService.getMember(memberId)
        val writerInfo =
            WriterInfo.of(
                writerId = member.id,
                writerNickname = member.name,
                writerProfileImageUrl = member.profileImageUrl
            )

        val comment =
            Comment.of(
                id = idGenerator.nextId(),
                postId = postId,
                writerInfo = writerInfo,
                content = content
            )

        return commentRepository.save(comment).id
    }

    /**
     * 댓글 수정
     */
    fun updateComment(
        memberId: Long,
        commentId: Long,
        content: String
    ) {
        val comment = commentRepository.getByIdOrThrow(commentId)
        validateOwner(comment, memberId)
        comment.update(content)
    }

    /**
     * 댓글 삭제
     */
    fun deleteComment(
        memberId: Long,
        commentId: Long
    ) {
        val comment = commentRepository.getByIdOrThrow(commentId)
        validateOwner(comment, memberId)
        commentRepository.delete(comment)
    }

    private fun validateOwner(
        comment: Comment,
        memberId: Long
    ) {
        if (!comment.isOwner(memberId)) {
            throw GeneralException(ErrorStatus.FORBIDDEN)
        }
    }
}
