package kr.spot.member.domain.vo

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import kr.spot.common.api.exception.GeneralException
import kr.spot.common.api.status.ErrorStatus

@Embeddable
data class Email(
    @Column(nullable = false, name = "email")
    val value: String
) {
    init {
        require(value.isNotBlank() && EMAIL_PATTERN.matches(value)) {
            throw GeneralException(ErrorStatus.INVALID_EMAIL_FORMAT)
        }
    }

    companion object {
        private val EMAIL_PATTERN = Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")

        fun of(value: String): Email = Email(value)
    }
}
