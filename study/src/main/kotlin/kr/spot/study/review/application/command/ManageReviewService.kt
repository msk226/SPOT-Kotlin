package kr.spot.study.review.application.command

import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.exception.GeneralException
import kr.spot.common.id.IdGenerator
import kr.spot.common.ports.GetWriterInfoPort
import kr.spot.study.core.application.validator.StudyAccessValidator
import kr.spot.study.review.domain.Review
import kr.spot.study.review.domain.vo.Content
import kr.spot.study.review.domain.vo.WriterInfo
import kr.spot.study.review.infrastructure.jpa.ReviewRepository
import kr.spot.study.review.presentation.command.dto.CreateReviewRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ManageReviewService(
    private val idGenerator: IdGenerator,
    private val getWriterInfoPort: GetWriterInfoPort,
    private val reviewRepository: ReviewRepository,
    private val studyAccessValidator: StudyAccessValidator
) {
    fun createReview(
        studyId: Long,
        memberId: Long,
        request: CreateReviewRequest,
    ): Long {
        studyAccessValidator.validateStudyMember(studyId, memberId)

        val writerInfo = getWriterInfo(memberId)
        val content = Content.of(request.activity, request.learned, request.encouragement, null)

        val reviewId = idGenerator.nextId()
        val review = Review.of(reviewId, studyId, writerInfo, content, request.isPrivate)
        reviewRepository.save(review)
        return reviewId
    }

    fun deleteReview(
        studyId: Long,
        reviewId: Long,
        memberId: Long
    ) {
        studyAccessValidator.validateStudyMember(studyId, memberId)

        val review = getByIdOrThrow(reviewId)
        review.writerInfo.validateIsOwnMember(memberId)

        reviewRepository.delete(review)
    }

    private fun getWriterInfo(memberId: Long): WriterInfo {
        val response = getWriterInfoPort.get(memberId)
        return WriterInfo.of(response.writerId, response.nickname, response.profileImageUrl)
    }

    fun getByIdOrThrow(reviewId: Long): Review =
        reviewRepository.findById(reviewId).orElseThrow { GeneralException(ErrorStatus.REVIEW_NOT_FOUND) }
}
