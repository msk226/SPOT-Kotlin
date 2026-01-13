package kr.spot.study.review.presentation.command.dto

data class CreateReviewRequest(
    val activity: String,
    val learned: String,
    val encouragement: String,
    val isPrivate: Boolean?
)
