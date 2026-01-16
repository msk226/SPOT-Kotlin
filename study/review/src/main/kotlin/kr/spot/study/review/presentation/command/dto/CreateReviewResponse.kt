package kr.spot.study.review.presentation.command.dto

data class CreateReviewResponse(
    val reviewId: Long
) {
    companion object {
        fun from(reviewId: Long): CreateReviewResponse = CreateReviewResponse(reviewId)
    }
}
