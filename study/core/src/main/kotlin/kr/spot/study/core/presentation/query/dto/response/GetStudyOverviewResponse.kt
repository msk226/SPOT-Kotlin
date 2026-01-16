package kr.spot.study.core.presentation.query.dto.response

data class GetStudyOverviewResponse(
    val content: List<StudyOverview>,
    val hasNext: Boolean,
    val nextCursor: Long?,
    val totalElements: Long
) {
    data class StudyOverview(
        val id: Long,
        val name: String,
        val description: String,
        val maxMembers: Int,
        val currentMembers: Int,
        val likeCount: Long,
        val isLiked: Boolean,
        val hitCount: Long,
        val profileImageUrl: String?
    ) {
        companion object {
            @Suppress("LongParameterList")
            fun of(
                id: Long,
                name: String,
                description: String,
                maxMembers: Int,
                currentMembers: Int,
                isLiked: Boolean,
                likeCount: Long,
                hitCount: Long,
                imageUrl: String?
            ): StudyOverview =
                StudyOverview(
                    id = id,
                    name = name,
                    description = description,
                    maxMembers = maxMembers,
                    currentMembers = currentMembers,
                    likeCount = likeCount,
                    isLiked = isLiked,
                    hitCount = hitCount,
                    profileImageUrl = imageUrl
                )
        }
    }

    companion object {
        fun of(
            content: List<StudyOverview>,
            hasNext: Boolean,
            nextCursor: Long?,
            totalElements: Long?
        ): GetStudyOverviewResponse =
            GetStudyOverviewResponse(
                content = content,
                hasNext = hasNext,
                nextCursor = nextCursor,
                totalElements = totalElements ?: 0L
            )
    }
}
