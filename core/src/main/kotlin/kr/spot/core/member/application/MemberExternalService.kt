package kr.spot.core.member.application

import kr.spot.common.id.IdGenerator
import kr.spot.core.member.domain.Member
import kr.spot.core.member.domain.enums.LoginType
import kr.spot.core.member.domain.vo.Email
import kr.spot.core.member.infrastructure.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 외부 모듈(auth, study 등)에서 호출하는 서비스
 */
@Service
class MemberExternalService(
    private val idGenerator: IdGenerator,
    private val memberRepository: MemberRepository
) {
    /**
     * OAuth 로그인 후 회원 조회 또는 생성
     * - 기존 회원이면 ID 반환
     * - 신규 회원이면 생성 후 ID 반환
     */
    @Transactional
    fun ensureFromOAuth(
        provider: String,
        email: String,
        nickname: String,
        imageUrl: String?
    ): Long {
        val loginType = LoginType.valueOf(provider.uppercase())
        val emailVo = Email.of(email)

        // 기존 회원 확인
        val existingMember = memberRepository.findByEmailAndLoginType(emailVo, loginType)
        if (existingMember != null) {
            return existingMember.id
        }

        // 신규 회원 생성
        val member = Member.of(
            id = idGenerator.nextId(),
            email = emailVo,
            name = nickname,
            loginType = loginType,
            profileImageUrl = imageUrl
        )
        return memberRepository.save(member).id
    }

    /**
     * 여러 회원 정보 조회 (study 모듈에서 사용)
     */
    @Transactional(readOnly = true)
    fun getMemberInfoMap(memberIds: List<Long>): Map<Long, MemberInfo> {
        return memberRepository.findAllById(memberIds)
            .associate { it.id to MemberInfo(it.name, it.profileImageUrl) }
    }

    /**
     * 작성자 정보 조회 (study 모듈에서 사용)
     */
    @Transactional(readOnly = true)
    fun getWriterInfo(memberId: Long): WriterInfo {
        val member = memberRepository.findById(memberId).orElse(null)
            ?: return WriterInfo(memberId, "알 수 없음", null)
        return WriterInfo(member.id, member.name, member.profileImageUrl)
    }

    data class MemberInfo(
        val name: String,
        val profileImageUrl: String?
    )

    data class WriterInfo(
        val memberId: Long,
        val name: String,
        val profileImageUrl: String?
    )
}
