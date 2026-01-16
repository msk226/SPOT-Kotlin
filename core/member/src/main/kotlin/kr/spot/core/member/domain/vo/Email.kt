package kr.spot.core.member.domain.vo

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
        require(
            value.isNotBlank() &&
                _root_ide_package_.kr.spot.core.member.domain.vo.Email.Companion.EMAIL_PATTERN
                    .matches(value)
        ) {
            throw GeneralException(ErrorStatus.INVALID_EMAIL_FORMAT)
        }
    }

    companion object {
        private val EMAIL_PATTERN = Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")

        fun of(value: String): kr.spot.core.member.domain.vo.Email =
            _root_ide_package_.kr.spot.core.member.domain.vo
                .Email(value)
    }
}
