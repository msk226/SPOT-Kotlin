package kr.spot.study.core.application.validator

import kr.spot.common.api.exception.GeneralException
import kr.spot.common.api.status.ErrorStatus
import kr.spot.study.core.domain.enums.StudyMemberStatus
import kr.spot.study.core.infrastructure.jpa.StudyMemberRepository
import kr.spot.study.core.infrastructure.jpa.StudyRepository
import org.springframework.stereotype.Component

@Component
class StudyAccessValidator(
    private val studyRepository: StudyRepository,
    private val studyMemberRepository: StudyMemberRepository
) {
    fun validateStudyLeader(
        studyId: Long,
        memberId: Long
    ) {
        val study = studyRepository.getStudyById(studyId)
        study.validateIsStudyOwner(memberId)
    }

    fun validateStudyMember(
        studyId: Long,
        memberId: Long
    ) {
        val isMember = isStudyMember(studyId, memberId)

        if (!isMember) {
            throw GeneralException(ErrorStatus.STUDY_ACCESS_DENIED)
        }
    }

    fun isStudyMember(
        studyId: Long,
        memberId: Long
    ): Boolean =
        studyMemberRepository.existsByStudyIdAndMemberIdAndStudyMemberStatusIn(
            studyId,
            memberId,
            listOf(StudyMemberStatus.OWNER, StudyMemberStatus.APPROVED)
        )
}
