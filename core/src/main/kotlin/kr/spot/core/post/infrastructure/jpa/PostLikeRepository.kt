package kr.spot.core.post.infrastructure.jpa

import kr.spot.core.post.domain.association.PostLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PostLikeRepository : JpaRepository<PostLike, Long> {
    @Modifying
    @Query(
        value = """
            INSERT IGNORE INTO post_like(
                id, post_id, member_id, status, created_at, updated_at
            )
            VALUES (:id, :postId, :memberId, 'ACTIVE', NOW(), NOW())
        """,
        nativeQuery = true
    )
    fun savePostLike(
        @Param("id") id: Long,
        @Param("postId") postId: Long,
        @Param("memberId") memberId: Long
    ): Int

    @Modifying
    @Query(
        value = """
            DELETE FROM post_like
             WHERE post_id = :postId
               AND member_id = :memberId
        """,
        nativeQuery = true
    )
    fun hardDelete(
        @Param("postId") postId: Long,
        @Param("memberId") memberId: Long
    ): Int

    @Modifying
    @Query(
        value = "DELETE FROM post_like WHERE member_id = :memberId",
        nativeQuery = true
    )
    fun deleteAllByMemberId(
        @Param("memberId") memberId: Long
    )

    fun existsByPostIdAndMemberId(
        postId: Long,
        memberId: Long
    ): Boolean

    @Query("select pl.postId from PostLike pl where pl.memberId = :memberId and pl.postId in :postIds")
    fun findLikedPostIds(
        @Param("memberId") memberId: Long,
        @Param("postIds") postIds: List<Long>
    ): List<Long>
}
