package kr.spot.core.member.presentation.dto.request

data class UpdateMemberNameRequest(
    val name: String
)

data class RegisterPreferredCategoryRequest(
    val categories: List<String>
)

data class RegisterPreferredRegionRequest(
    val regionCodes: List<String>
)

data class CreateTestMemberRequest(
    val name: String,
    val email: String
)
