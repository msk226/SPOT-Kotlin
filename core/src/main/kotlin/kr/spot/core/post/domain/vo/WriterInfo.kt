package kr.spot.core.post.domain.vo

import jakarta.persistence.Embeddable

@Embeddable
class WriterInfo(
    val writerId: Long,
    val writerNickname: String,
    val writerProfileImageUrl: String?
) {
    companion object {
        fun of(
            writerId: Long,
            writerNickname: String,
            writerProfileImageUrl: String?
        ): WriterInfo =
            WriterInfo(
                writerId,
                writerNickname,
                writerProfileImageUrl
            )
    }
}
