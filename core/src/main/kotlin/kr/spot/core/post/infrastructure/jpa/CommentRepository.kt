package kr.spot.core.post.infrastructure.jpa

import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.api.exception.GeneralException
import kr.spot.core.post.domain.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CommentRepository : JpaRepository<Comment, Long> {
    @Query(
        "select c from Comment c " +
            "where c.postId = :postId and c.status = 'ACTIVE' " +
            "order by c.createdAt asc"
    )
    fun getCommentsByPostId(
        @Param("postId") postId: Long
    ): List<Comment>

    fun deleteByWriterInfoWriterId(writerId: Long)

    @Modifying
    @Query(
        """
        update Comment c
           set c.content = :content,
               c.updatedAt = CURRENT_TIMESTAMP
         where c.id = :commentId
           and c.writerInfo.writerId = :writerId
           and c.status = 'ACTIVE'
    """
    )
    fun updateComment(
        @Param("commentId") commentId: Long,
        @Param("content") content: String,
        @Param("writerId") writerId: Long
    ): Int

    @Modifying
    @Query(
        """
        update Comment c
           set c.status = 'INACTIVE',
               c.updatedAt = CURRENT_TIMESTAMP
         where c.id = :commentId
           and c.writerInfo.writerId = :writerId
           and c.status = 'ACTIVE'
    """
    )
    fun deleteComment(
        @Param("commentId") commentId: Long,
        @Param("writerId") writerId: Long
    ): Int
}

fun CommentRepository.getByIdOrThrow(id: Long): Comment =
    findById(id).orElseThrow {
        GeneralException(ErrorStatus.COMMENT_NOT_FOUND)
    }
