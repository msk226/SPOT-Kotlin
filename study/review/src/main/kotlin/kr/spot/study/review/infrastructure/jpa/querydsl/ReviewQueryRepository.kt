package kr.spot.study.review.infrastructure.jpa.querydsl

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.spot.study.review.domain.QReview
import kr.spot.study.review.domain.Review
import kr.spot.study.review.domain.associations.QReviewReaction
import kr.spot.study.review.domain.enums.Reaction
import org.springframework.stereotype.Repository

@Repository
class ReviewQueryRepository(
    private val query: JPAQueryFactory
) {
    private val review = QReview.review
    private val reviewReaction = QReviewReaction.reviewReaction

    fun findByStudyIdWithCursor(
        studyId: Long,
        cursor: Long?,
        limit: Int
    ): List<Review> =
        query
            .selectFrom(review)
            .where(
                review.studyId.eq(studyId),
                ltCursor(cursor)
            ).orderBy(review.id.desc())
            .limit(limit.toLong())
            .fetch()

    private fun ltCursor(cursor: Long?): BooleanExpression? = cursor?.let { review.id.lt(it) }

    fun countByStudyId(studyId: Long): Long =
        query
            .select(review.count())
            .from(review)
            .where(review.studyId.eq(studyId))
            .fetchOne() ?: 0L

    fun findReactionCountsByReviewIds(reviewIds: Collection<Long>): Map<Long, ReactionCounts> {
        if (reviewIds.isEmpty()) {
            return emptyMap()
        }

        val rows =
            query
                .select(
                    Projections.constructor(
                        ReactionCountRow::class.java,
                        reviewReaction.reviewId,
                        reviewReaction.reaction,
                        reviewReaction.count()
                    )
                ).from(reviewReaction)
                .where(reviewReaction.reviewId.`in`(reviewIds))
                .groupBy(reviewReaction.reviewId, reviewReaction.reaction)
                .fetch()

        return rows
            .groupBy { it.reviewId }
            .mapValues { (_, rowList) -> toReactionCounts(rowList) }
    }

    private fun toReactionCounts(rows: List<ReactionCountRow>): ReactionCounts {
        var fire = 0L
        var heart = 0L
        var star = 0L
        var smile = 0L
        for (row in rows) {
            when (row.reaction) {
                Reaction.FIRE -> fire = row.count
                Reaction.HEART -> heart = row.count
                Reaction.STAR -> star = row.count
                Reaction.SMILE -> smile = row.count
            }
        }
        return ReactionCounts(fire, heart, star, smile)
    }

    fun findMemberReactionsByReviewIds(
        memberId: Long?,
        reviewIds: Collection<Long>
    ): Map<Long, Set<Reaction>> {
        if (memberId == null || reviewIds.isEmpty()) {
            return emptyMap()
        }

        return query
            .select(reviewReaction.reviewId, reviewReaction.reaction)
            .from(reviewReaction)
            .where(
                reviewReaction.memberId.eq(memberId),
                reviewReaction.reviewId.`in`(reviewIds)
            ).fetch()
            .groupBy(
                { it.get(reviewReaction.reviewId)!! },
                { it.get(reviewReaction.reaction)!! }
            ).mapValues { (_, reactions) -> reactions.toSet() }
    }

    data class ReactionCountRow(
        val reviewId: Long,
        val reaction: Reaction,
        val count: Long
    )

    data class ReactionCounts(
        val fire: Long,
        val heart: Long,
        val star: Long,
        val smile: Long
    )
}
