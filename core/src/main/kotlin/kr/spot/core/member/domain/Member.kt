package kr.spot.core.member.domain

import jakarta.persistence.*
import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.exception.GeneralException
import kr.spot.core.member.domain.enums.LoginType
import kr.spot.core.member.domain.vo.Email
import kr.spot.core.global.domain.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "member")
@SQLDelete(sql = "UPDATE member SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class Member private constructor(
    @Id
    val id: Long,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "email", nullable = false, unique = true))
    val email: Email,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val loginType: LoginType,

    @Column
    var profileImageUrl: String? = null
) : BaseEntity() {

    fun updateName(newName: String) {
        require(newName.isNotBlank()) {
            throw GeneralException(ErrorStatus.NAME_CAN_NOT_NULL_OR_EMPTY)
        }
        this.name = newName
    }

    companion object {
        fun of(
            id: Long,
            email: Email,
            name: String,
            loginType: LoginType,
            profileImageUrl: String? = null
        ): Member {
            require(name.isNotBlank()) {
                throw GeneralException(ErrorStatus.NAME_CAN_NOT_NULL_OR_EMPTY)
            }
            return Member(id, email, name, loginType, profileImageUrl)
        }
    }
}
