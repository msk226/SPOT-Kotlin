package kr.spot.study.core.application.event

import kr.spot.common.domain.events.MemberWithdrawnEvent
import kr.spot.study.core.infrastructure.jpa.StudyLikeRepository
import kr.spot.study.core.infrastructure.jpa.StudyMemberRepository
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Order(1)
@Component
class CleanupStudyMembershipOnMemberWithdrawn(
    private val studyMemberRepository: StudyMemberRepository,
    private val studyLikeRepository: StudyLikeRepository
) {
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handle(event: MemberWithdrawnEvent) {
        val memberId = event.memberId
        studyMemberRepository.deleteByMemberId(memberId)
        studyLikeRepository.deleteAllByMemberId(memberId)
    }
}
