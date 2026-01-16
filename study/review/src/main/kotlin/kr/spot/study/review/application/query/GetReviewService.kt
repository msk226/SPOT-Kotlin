package kr.spot.study.review.application.query

import kr.spot.study.core.application.validator.StudyAccessValidator
import kr.spot.study.review.domain.Review
import kr.spot.study.review.domain.enums.Reaction
import kr.spot.study.review.domain.vo.Content
import kr.spot.study.review.infrastructure.jpa.querydsl.ReviewQueryRepository
import kr.spot.study.review.infrastructure.jpa.querydsl.ReviewQueryRepository.ReactionCounts
import kr.spot.study.review.presentation.query.dto.GetReviewListResponse
import kr.spot.study.review.presentation.query.dto.GetReviewListResponse.ContentResponse
import kr.spot.study.review.presentation.query.dto.GetReviewListResponse.ReactionCountResponse
import kr.spot.study.review.presentation.query.dto.GetReviewListResponse.ReactionResponse
import kr.spot.study.review.presentation.query.dto.GetReviewListResponse.ReviewResponse
import kr.spot.study.review.presentation.query.dto.GetReviewListResponse.WriterInfoResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetReviewService(
    private val reviewQueryRepository: ReviewQueryRepository,
    private val studyAccessValidator: StudyAccessValidator
) {
    fun getReviewList(
        studyId: Long,
        viewerId: Long?,
        cursor: Long?,
        size: Int
    ): GetReviewListResponse {
        val isStudyMember = viewerId != null && studyAccessValidator.isStudyMember(studyId, viewerId)
        var reviews = reviewQueryRepository.findByStudyIdWithCursor(studyId, cursor, size + 1)

        val hasNext = reviews.size > size
        if (hasNext) {
            reviews = reviews.subList(0, size)
        }

        val reviewIds = reviews.map { it.id }
        val reactionCountsMap = reviewQueryRepository.findReactionCountsByReviewIds(reviewIds)
        val memberReactionsMap = reviewQueryRepository.findMemberReactionsByReviewIds(viewerId, reviewIds)

        val totalElements = reviewQueryRepository.countByStudyId(studyId)
        val nextCursor = if (hasNext && reviews.isNotEmpty()) reviews.last().id else null

        val reviewResponses =
            reviews.map { review ->
                toReviewResponse(review, reactionCountsMap, memberReactionsMap, isStudyMember)
            }

        return GetReviewListResponse(reviewResponses, hasNext, nextCursor, totalElements)
    }

    @Suppress("LongParameterList")
    private fun toReviewResponse(
        review: Review,
        reactionCountsMap: Map<Long, ReactionCounts>,
        memberReactionsMap: Map<Long, Set<Reaction>>,
        isStudyMember: Boolean
    ): ReviewResponse {
        val writerInfo = review.writerInfo
        val content = review.content
        val counts = reactionCountsMap.getOrDefault(review.id, ReactionCounts(0, 0, 0, 0))
        val memberReactions = memberReactionsMap.getOrDefault(review.id, emptySet())

        val contentResponse = createContentResponse(review, content, isStudyMember)

        return ReviewResponse.from(
            review.id,
            WriterInfoResponse.from(
                writerInfo.writerId,
                writerInfo.writerName,
                writerInfo.writerProfileImageUrl
            ),
            contentResponse,
            ReactionCountResponse.from(counts.fire, counts.heart, counts.star, counts.smile),
            ReactionResponse.from(
                memberReactions.contains(Reaction.FIRE),
                memberReactions.contains(Reaction.HEART),
                memberReactions.contains(Reaction.STAR),
                memberReactions.contains(Reaction.SMILE)
            ),
            review.isPrivate()
        )
    }

    private fun createContentResponse(
        review: Review,
        content: Content,
        isStudyMember: Boolean
    ): ContentResponse {
        if (review.isPrivate() && !isStudyMember) {
            return ContentResponse.from(
                PRIVATE_CONTENT_MESSAGE,
                PRIVATE_CONTENT_MESSAGE,
                PRIVATE_CONTENT_MESSAGE,
                null
            )
        }
        return ContentResponse.from(
            content.activity,
            content.learned,
            content.encouragement,
            content.imageUrl
        )
    }

    companion object {
        private const val PRIVATE_CONTENT_MESSAGE = "이 글은 스터디원에게만 노출됩니다."
    }
}
