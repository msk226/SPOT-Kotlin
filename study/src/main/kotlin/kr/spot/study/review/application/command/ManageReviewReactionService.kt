package kr.spot.study.review.application.command

import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.api.exception.GeneralException
import kr.spot.common.id.IdGenerator
import kr.spot.study.core.application.validator.StudyAccessValidator
import kr.spot.study.review.domain.associations.ReviewReaction
import kr.spot.study.review.domain.enums.Reaction
import kr.spot.study.review.infrastructure.jpa.ReviewReactionRepository
import kr.spot.study.review.infrastructure.jpa.ReviewRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ManageReviewReactionService(
    private val idGenerator: IdGenerator,
    private val reviewRepository: ReviewRepository,
    private val reviewReactionRepository: ReviewReactionRepository,
    private val studyAccessValidator: StudyAccessValidator
) {
    fun addReaction(
        studyId: Long,
        reviewId: Long,
        memberId: Long,
        reaction: Reaction
    ) {
        studyAccessValidator.validateStudyMember(studyId, memberId)
        validateExists(reviewId)

        if (reviewReactionRepository.existsByReviewIdAndMemberIdAndReaction(reviewId, memberId, reaction)) {
            return
        }

        val reviewReaction = ReviewReaction.of(idGenerator.nextId(), reviewId, memberId, reaction)
        reviewReactionRepository.save(reviewReaction)
    }

    fun removeReaction(
        studyId: Long,
        reviewId: Long,
        memberId: Long,
        reaction: Reaction
    ) {
        studyAccessValidator.validateStudyMember(studyId, memberId)
        validateExists(reviewId)

        reviewReactionRepository.hardDelete(reviewId, memberId, reaction.name)
    }

    fun validateExists(reviewId: Long) {
        if (!reviewRepository.existsById(reviewId)) {
            throw GeneralException(ErrorStatus.REVIEW_NOT_FOUND)
        }
    }
}
