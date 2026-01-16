package kr.spot.study.core.presentation.query.dto.response

data class GetAppliesResponse(
    val applies: List<Apply>
) {
    data class Apply(
        val applicantId: Long,
        val memberId: Long,
        val nickname: String,
        val description: String?,
        val profileImageUrl: String?
    ) {
        companion object {
            fun of(
                applicantId: Long,
                memberId: Long,
                nickname: String,
                description: String?,
                profileImageUrl: String?
            ): Apply = Apply(applicantId, memberId, nickname, description, profileImageUrl)
        }
    }

    companion object {
        fun of(applies: List<Apply>): GetAppliesResponse = GetAppliesResponse(applies)
    }
}
