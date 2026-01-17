package kr.spot.core.member.application

import kr.spot.common.api.exception.GeneralException
import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.event.payload.MemberCreatedEvent
import kr.spot.common.event.payload.MemberProfileUpdatedEvent
import kr.spot.common.event.publisher.KafkaEventPublisher
import kr.spot.common.id.IdGenerator
import kr.spot.core.member.domain.Member
import kr.spot.core.member.domain.PreferredCategory
import kr.spot.core.member.domain.enums.LoginType
import kr.spot.core.member.domain.vo.Email
import kr.spot.core.member.infrastructure.MemberRepository
import kr.spot.core.member.infrastructure.PreferredCategoryRepository
import kr.spot.core.member.presentation.command.dto.request.UpdateMemberInfoRequest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberCommandService(
    private val idGenerator: IdGenerator,
    private val memberRepository: MemberRepository,
    private val preferredCategoryRepository: PreferredCategoryRepository,
    private val eventPublisher: KafkaEventPublisher,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    /**
     * 테스트용 회원 생성
     */
    fun createTestMember(
        name: String,
        email: String
    ): Long {
        val member =
            Member.of(
                id = idGenerator.nextId(),
                email =
                    Email
                        .of(email),
                name = name,
                loginType = LoginType.KAKAO, // 테스트용 기본값
                profileImageUrl = null
            )
        applicationEventPublisher.publishEvent(MemberCreatedEvent(member.id))
        return memberRepository.save(member).id
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
     * 회원 프로필 업데이트
     * 프로필 변경 시 MemberProfileUpdatedEvent 발행
     */
    fun updateProfile(
        memberId: Long,
        updateMemberInfoRequest: UpdateMemberInfoRequest
    ) {
        val member =
            memberRepository
                .findById(memberId)
                .orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }

        val name = updateMemberInfoRequest.name ?: member.name
        val profileImageUrl = updateMemberInfoRequest.profileImageUrl ?: member.profileImageUrl

        member.updateProfile(name, profileImageUrl)

        eventPublisher.publish(
            key = memberId.toString(),
            event =
                MemberProfileUpdatedEvent(
                    memberId = memberId,
                    nickname = name,
                    profileImageUrl = profileImageUrl
                )
        )
    }

    /**
     * 회원 선호 카테고리 등록
     */
    fun registerPreferCategories(
        memberId: Long,
        categories: List<String>
    ) {
        // 기존 선호 카테고리 삭제
        preferredCategoryRepository.deleteAllByMemberId(memberId)

        // 새로운 선호 카테고리 저장
        val preferredCategories =
            categories.map { category ->
                PreferredCategory.of(
                    id = idGenerator.nextId(),
                    memberId = memberId,
                    category = category
                )
            }
        preferredCategoryRepository.saveAll(preferredCategories)
    }
}
