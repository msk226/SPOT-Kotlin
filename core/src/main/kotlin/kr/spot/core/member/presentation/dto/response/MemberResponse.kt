package kr.spot.core.member.presentation.dto.response

import kr.spot.core.member.domain.Member
import kr.spot.core.member.domain.enums.LoginType

data class GetMemberNameResponse(
    val name: String
) {
    companion object {
        fun from(name: String) = GetMemberNameResponse(name)
    }
}

data class GetMemberInfoResponse(
    val memberId: Long,
    val nickname: String,
    val profileImageUrl: String?,
    val loginType: LoginType,
    val email: String
) {
    companion object {
        fun from(member: Member) = GetMemberInfoResponse(
            memberId = member.id,
            nickname = member.name,
            profileImageUrl = member.profileImageUrl,
            loginType = member.loginType,
            email = member.email.value
        )
    }
}

data class GetMemberPreferCategoryResponse(
    val categories: List<String>,
    val totalCount: Int
) {
    companion object {
        fun from(categories: List<String>) = GetMemberPreferCategoryResponse(
            categories = categories,
            totalCount = categories.size
        )
    }
}

data class GetMemberPreferRegionResponse(
    val regionCodes: List<String>,
    val totalCount: Int
) {
    companion object {
        fun from(regionCodes: List<String>) = GetMemberPreferRegionResponse(
            regionCodes = regionCodes,
            totalCount = regionCodes.size
        )
    }
}
