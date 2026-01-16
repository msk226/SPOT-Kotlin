package kr.spot.post.domain.vo

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import kr.spot.common.api.exception.GeneralException
import kr.spot.common.api.status.ErrorStatus

@Embeddable
class WriterInfo(
    @Column(name = "writer_id")
    val writerId: Long,
    @Column(name = "writer_nickname")
    val writerNickname: String,
    @Column(name = "writer_profile_image_url")
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
