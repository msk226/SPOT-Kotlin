package kr.spot.study.core.presentation.query.dto.response

data class GetStudyMembersResponse(
    val members: List<MemberResponse>,
    val totalMembers: Long
) {
    data class MemberResponse(
        val memberId: Long,
        val nickname: String,
        val profileImageUrl: String?,
        val isOwner: Boolean
    ) {
        companion object {
            fun of(
                memberId: Long,
                nickname: String,
                profileImageUrl: String?,
                isOwner: Boolean
            ): MemberResponse = MemberResponse(memberId, nickname, profileImageUrl, isOwner)
        }
    }

    companion object {
        fun of(
            members: List<MemberResponse>,
            totalMembers: Long
        ): GetStudyMembersResponse = GetStudyMembersResponse(members, totalMembers)
    }
}
