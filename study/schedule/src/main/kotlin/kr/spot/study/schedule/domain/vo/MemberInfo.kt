package kr.spot.study.schedule.domain.vo

import jakarta.persistence.Embeddable

@Embeddable
data class MemberInfo(
    val memberId: Long = 0L,
    val memberName: String = "",
    val memberProfileImageUrl: String? = null
) {
    companion object {
        fun of(
            memberId: Long,
            memberName: String,
            memberProfileImageUrl: String?
        ): MemberInfo = MemberInfo(memberId, memberName, memberProfileImageUrl)
    }
}
