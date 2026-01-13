package kr.spot.common.ports.dto

data class MemberInfoResponse(
    val name: String,
    val profileImageUrl: String?
) {
    companion object {
        fun of(
            name: String,
            profileImageUrl: String?
        ): MemberInfoResponse = MemberInfoResponse(name, profileImageUrl)
    }
}
