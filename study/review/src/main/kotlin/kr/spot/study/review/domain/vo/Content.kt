package kr.spot.study.review.domain.vo

import jakarta.persistence.Embeddable

@Embeddable
data class Content(
    val activity: String = "",
    val learned: String = "",
    val encouragement: String = "",
    val imageUrl: String? = null
) {
    companion object {
        fun of(
            activity: String,
            learned: String,
            encouragement: String,
            imageUrl: String?
        ): Content = Content(activity, learned, encouragement, imageUrl)
    }
}
