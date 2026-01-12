package kr.spot.core.member.application

import kr.spot.core.member.domain.Member
import kr.spot.core.member.infrastructure.MemberRepository
import kr.spot.core.member.infrastructure.PreferredCategoryRepository
import kr.spot.core.member.infrastructure.PreferredRegionRepository
import kr.spot.core.member.infrastructure.findByIdOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberQueryService(
    private val memberRepository: MemberRepository,
    private val preferredCategoryRepository: PreferredCategoryRepository,
    private val preferredRegionRepository: PreferredRegionRepository
) {
    /**
     * 회원 정보 조회
     */
    fun getMember(memberId: Long): Member = memberRepository.findByIdOrThrow(memberId)

    /**
     * 회원 이름 조회
     */
    fun getMemberName(memberId: Long): String {
        val member = memberRepository.findByIdOrThrow(memberId)
        return member.name
    }

    /**
     * 선호 카테고리 조회
     */
    fun getPreferredCategories(memberId: Long): List<String> =
        preferredCategoryRepository
            .findAllByMemberId(memberId)
            .map { it.category }

    /**
     * 선호 지역 조회
     */
    fun getPreferredRegions(memberId: Long): List<String> =
        preferredRegionRepository
            .findAllByMemberId(memberId)
            .map { it.regionCode }
}
