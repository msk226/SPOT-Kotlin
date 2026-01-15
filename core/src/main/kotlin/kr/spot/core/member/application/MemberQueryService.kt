package kr.spot.core.member.application

import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.api.exception.GeneralException
import kr.spot.core.member.domain.Member
import kr.spot.core.member.infrastructure.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberQueryService(
    private val memberRepository: MemberRepository
) {
    /**
     * 회원 정보 조회
     */
    fun getMember(memberId: Long): Member = findByIdOrThrow(memberId)

    /**
     * 회원 이름 조회
     */
    fun getMemberName(memberId: Long): String {
        val member = findByIdOrThrow(memberId)
        return member.name
    }

    fun findByIdOrThrow(id: Long): Member =
        memberRepository.findById(id).orElseThrow {
            GeneralException(ErrorStatus.MEMBER_NOT_FOUND)
        }
}
