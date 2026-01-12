package kr.spot.core.member.infrastructure

import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.exception.GeneralException
import kr.spot.core.member.domain.Member
import kr.spot.core.member.domain.enums.LoginType
import kr.spot.core.member.domain.vo.Email
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {
    fun existsByEmailAndLoginType(
        email: Email,
        loginType: LoginType
    ): Boolean

    fun findByEmailAndLoginType(
        email: Email,
        loginType: LoginType
    ): Member?
}

fun MemberRepository.findByIdOrThrow(id: Long): Member =
    findById(id).orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }

fun MemberRepository.findByEmailAndLoginTypeOrThrow(
    email: Email,
    loginType: LoginType
): Member =
    findByEmailAndLoginType(email, loginType)
        ?: throw GeneralException(ErrorStatus.MEMBER_NOT_FOUND)
