package kr.spot.core.post.domain.vo

import jakarta.persistence.Embeddable
import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.api.exception.GeneralException

@Embeddable
class WriterInfo(
    val writerId: Long,
    val writerNickname: String,
    val writerProfileImageUrl: String?
) {
    fun validateIsOwnMember(memberId: Long) {
        if (writerId != memberId) {
            throw GeneralException(ErrorStatus.ONLY_AUTHOR_CAN_MODIFY)
        }
    }

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
