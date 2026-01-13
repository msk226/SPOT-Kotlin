package kr.spot.common.ports.dto

data class WriterInfoResponse(
    val writerId: Long,
    val nickname: String,
    val profileImageUrl: String?
) {
    companion object {
        fun of(
            writerId: Long,
            nickname: String,
            profileImageUrl: String?
        ): WriterInfoResponse = WriterInfoResponse(writerId, nickname, profileImageUrl)
    }
}
