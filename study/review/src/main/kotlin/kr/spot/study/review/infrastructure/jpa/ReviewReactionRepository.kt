package kr.spot.study.review.infrastructure.jpa

import kr.spot.study.review.domain.associations.ReviewReaction
import kr.spot.study.review.domain.enums.Reaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ReviewReactionRepository : JpaRepository<ReviewReaction, Long> {
    fun existsByReviewIdAndMemberIdAndReaction(
        reviewId: Long,
        memberId: Long,
        reaction: Reaction
    ): Boolean

    @Modifying
    @Query(
        value = """
            DELETE FROM review_reaction
            WHERE review_id = :reviewId
              AND member_id = :memberId
              AND reaction = :reaction
        """,
        nativeQuery = true
    )
    fun hardDelete(
        @Param("reviewId") reviewId: Long,
        @Param("memberId") memberId: Long,
        @Param("reaction") reaction: String
    ): Int

    @Modifying
    @Query(value = "DELETE FROM review_reaction WHERE member_id = :memberId", nativeQuery = true)
    fun deleteAllByMemberId(
        @Param("memberId") memberId: Long
    )
}
