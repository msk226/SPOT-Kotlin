package kr.spot.core.member.application

import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.exception.GeneralException
import kr.spot.common.id.IdGenerator
import kr.spot.core.member.domain.PreferredCategory
import kr.spot.core.member.domain.PreferredRegion
import kr.spot.core.member.infrastructure.MemberRepository
import kr.spot.core.member.infrastructure.PreferredCategoryRepository
import kr.spot.core.member.infrastructure.PreferredRegionRepository
import kr.spot.core.member.infrastructure.findByIdOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberCommandService(
    private val idGenerator: IdGenerator,
    private val memberRepository: MemberRepository,
    private val preferredCategoryRepository: PreferredCategoryRepository,
    private val preferredRegionRepository: PreferredRegionRepository

) {
    /**
     * 회원 이름 수정
     */
    fun updateName(memberId: Long, newName: String) {
        val member = memberRepository.findByIdOrThrow(memberId)
        member.updateName(newName)
    }

    /**
     * 회원 탈퇴
     * TODO: 스터디장인 경우 탈퇴 불가 검증 (study 모듈 의존)
     * TODO: 탈퇴 후 이벤트 발행 (MemberWithdrawnEvent)
     */
    fun withdraw(memberId: Long) {
        // TODO: hasActiveStudyAsLeader 검증
        memberRepository.deleteById(memberId)
        // TODO: eventPublisher.publishEvent(MemberWithdrawnEvent(memberId))
    }

    /**
     * 회원 선호 카테고리 등록
     */
    fun registerPreferCategories(memberId: Long, categories: List<String>) {
        // 기존 선호 카테고리 삭제
        preferredCategoryRepository.deleteAllByMemberId(memberId)

        // 새로운 선호 카테고리 저장
        val preferredCategories = categories.map { category ->
            PreferredCategory.of(
                id = idGenerator.nextId(),
                memberId = memberId,
                category = category
            )
        }
        preferredCategoryRepository.saveAll(preferredCategories)
    }

    /**
     * 회원 선호 지역 등록
     */
    fun registerPreferRegions(memberId: Long, regions: List<String>) {
        // 기존 선호 지역 삭제
        preferredRegionRepository.deleteAllByMemberId(memberId)

        // 새로운 선호 지역 저장
        val preferredRegions = regions.map { region ->
            PreferredRegion.of(
                id = idGenerator.nextId(),
                memberId = memberId,
                regionCode = region
            )
        }
        preferredRegionRepository.saveAll(preferredRegions)
    }
}
