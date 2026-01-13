package kr.spot.study.core.presentation.query.dto.response

import kr.spot.study.core.domain.enums.Category

data class GetStudyInfoResponse(
    val id: Long,
    val title: String,
    val description: String,
    val thumbnailUrl: String?,
    val categories: List<Category>,
    val statistics: Statistics
) {
    data class Statistics(
        val totalMembers: Int,
        val currentMembers: Int,
        val likeCount: Long,
        val hitCount: Long
    ) {
        companion object {
            fun of(
                totalMembers: Int,
                currentMembers: Int,
                likeCount: Long,
                hitCount: Long
            ): Statistics = Statistics(totalMembers, currentMembers, likeCount, hitCount)
        }
    }

    companion object {
        fun of(
            id: Long,
            title: String,
            description: String,
            thumbnailUrl: String?,
            categories: List<Category>,
            statistics: Statistics
        ): GetStudyInfoResponse =
            GetStudyInfoResponse(
                id,
                title,
                description,
                thumbnailUrl,
                categories,
                statistics
            )
    }
}
