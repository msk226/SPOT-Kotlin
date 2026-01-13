package kr.spot.study.review.presentation.query.dto

data class GetReviewListResponse(
    val reviews: List<ReviewResponse>,
    val hasNext: Boolean,
    val nextCursor: Long?,
    val totalElements: Long
) {
    data class ReviewResponse(
        val reviewId: Long,
        val writer: WriterInfoResponse,
        val content: ContentResponse,
        val reactionCounts: ReactionCountResponse,
        val reactions: ReactionResponse,
        val isPrivate: Boolean
    ) {
        companion object {
            @Suppress("LongParameterList")
            fun from(
                reviewId: Long,
                writer: WriterInfoResponse,
                content: ContentResponse,
                reactionCounts: ReactionCountResponse,
                reactions: ReactionResponse,
                isPrivate: Boolean
            ): ReviewResponse =
                ReviewResponse(
                    reviewId,
                    writer,
                    content,
                    reactionCounts,
                    reactions,
                    isPrivate
                )
        }
    }

    data class WriterInfoResponse(
        val memberId: Long,
        val nickname: String,
        val profileImageUrl: String?
    ) {
        companion object {
            fun from(
                memberId: Long,
                nickname: String,
                profileImageUrl: String?
            ): WriterInfoResponse = WriterInfoResponse(memberId, nickname, profileImageUrl)
        }
    }

    data class ContentResponse(
        val activity: String,
        val learned: String,
        val encouragement: String,
        val imageUrl: String?
    ) {
        companion object {
            fun from(
                activity: String,
                learned: String,
                encouragement: String,
                imageUrl: String?
            ): ContentResponse = ContentResponse(activity, learned, encouragement, imageUrl)
        }
    }

    data class ReactionCountResponse(
        val fireCount: Long,
        val heartCount: Long,
        val starCount: Long,
        val smileCount: Long
    ) {
        companion object {
            fun from(
                fireCount: Long,
                heartCount: Long,
                starCount: Long,
                smileCount: Long
            ): ReactionCountResponse = ReactionCountResponse(fireCount, heartCount, starCount, smileCount)
        }
    }

    data class ReactionResponse(
        val isFired: Boolean,
        val isHearted: Boolean,
        val isStarred: Boolean,
        val isSmiled: Boolean
    ) {
        companion object {
            fun from(
                isFired: Boolean,
                isHearted: Boolean,
                isStarred: Boolean,
                isSmiled: Boolean
            ): ReactionResponse = ReactionResponse(isFired, isHearted, isStarred, isSmiled)
        }
    }
}
