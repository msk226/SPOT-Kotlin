package kr.spot.core.post.infrastructure.jpa

import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.exception.GeneralException
import kr.spot.core.post.domain.Post
import kr.spot.core.post.domain.enums.PostType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PostRepository : JpaRepository<Post, Long> {
    fun findByIdAndStatus(
        id: Long,
        status: String = "ACTIVE"
    ): Post?

    @Modifying
    @Query(
        """
            update Post p
               set p.title = :title,
                   p.content = :content,
                   p.postType = :postType,
                   p.updatedAt = CURRENT_TIMESTAMP
             where p.id = :postId
               and p.writerInfo.writerId = :writerId
               and p.status = 'ACTIVE'
        """
    )
    fun updatePost(
        @Param("postId") postId: Long,
        @Param("title") title: String,
        @Param("content") content: String,
        @Param("postType") postType: PostType,
        @Param("writerId") writerId: Long
    ): Int

    @Modifying
    @Query(
        """
            update Post p
               set p.status = 'INACTIVE',
                   p.updatedAt = CURRENT_TIMESTAMP
             where p.id = :postId
               and p.writerInfo.writerId = :writerId
               and p.status = 'ACTIVE'
        """
    )
    fun deletePost(
        @Param("postId") postId: Long,
        @Param("writerId") writerId: Long
    ): Int

    @Query("select p from Post p where p.id in :postIds and p.status = 'ACTIVE'")
    fun getPostsByIds(
        @Param("postIds") postIds: List<Long>
    ): List<Post>

    fun deleteByWriterInfoWriterId(writerId: Long)

    @Query(
        """
        select p from Post p
         where p.status = 'ACTIVE'
           and (:postType is null or p.postType = :postType)
           and (:cursor is null or p.id < :cursor)
         order by p.id desc
    """
    )
    fun findPostsWithCursor(
        @Param("cursor") cursor: Long?,
        @Param("postType") postType: PostType?,
        pageable: org.springframework.data.domain.Pageable
    ): List<Post>

    @Modifying
    @Query(
        """
        update Post p
           set p.postStats.views = p.postStats.views + :delta,
               p.updatedAt = CURRENT_TIMESTAMP
         where p.id = :postId and p.status = 'ACTIVE'
    """
    )
    fun increaseViewBy(
        @Param("postId") postId: Long,
        @Param("delta") delta: Long
    ): Int
}

fun PostRepository.findByIdOrThrow(id: Long): Post =
    findById(id).orElseThrow {
        GeneralException(ErrorStatus.POST_NOT_FOUND)
    }
