package kr.spot.study.review.domain.vo

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.api.exception.GeneralException

@Embeddable
data class WriterInfo(
    val writerId: Long = 0L,
    val writerName: String = "",
    @Column(name = "writer_profile_image_url")
    val writerProfileImageUrl: String? = null
) {
    fun validateIsOwnMember(currentUserId: Long) {
        if (writerId != currentUserId) {
            throw GeneralException(ErrorStatus.ONLY_AUTHOR_CAN_MODIFY)
        }
    }

    fun isSameWriter(memberId: Long): Boolean = writerId == memberId

    companion object {
        fun of(
            writerId: Long,
            writerName: String,
            writerProfileImageUrl: String?
        ): WriterInfo = WriterInfo(writerId, writerName, writerProfileImageUrl)
    }
}
