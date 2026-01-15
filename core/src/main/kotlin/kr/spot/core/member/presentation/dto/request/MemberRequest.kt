package kr.spot.core.member.presentation.dto.request

data class UpdateMemberInfoRequest(
    val name: String?,
    val profileImageUrl: String?
)

data class RegisterPreferredCategoryRequest(
    val categories: List<String>
)

data class CreateTestMemberRequest(
    val name: String,
    val email: String
)
