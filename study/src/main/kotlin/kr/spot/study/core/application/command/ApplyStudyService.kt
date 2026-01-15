package kr.spot.study.core.application.command

import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.api.exception.GeneralException
import kr.spot.common.id.IdGenerator
import kr.spot.study.core.domain.enums.Decision
import kr.spot.study.core.domain.enums.StudyMemberStatus
import kr.spot.study.core.infrastructure.jpa.StudyMemberRepository
import kr.spot.study.core.infrastructure.jpa.StudyRepository
import kr.spot.study.core.presentation.command.dto.request.ApplyStudyRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ApplyStudyService(
    private val idGenerator: IdGenerator,
    private val studyRepository: StudyRepository,
    private val studyMemberRepository: StudyMemberRepository
) {
    fun processStudyApplication(
        applicationId: Long,
        requesterId: Long,
        decision: Decision
    ) {
        val application = studyMemberRepository.getStudyMemberById(applicationId)
        val study = studyRepository.getStudyById(application.studyId)

        study.processApplication(application, requesterId, decision)

        // TODO: Publish StudyApplicationProcessedEvent when common module provides it
    }

    fun applyStudy(
        studyId: Long,
        memberId: Long,
        request: ApplyStudyRequest
    ) {
        val study = studyRepository.getStudyById(studyId)
        validateIsAlreadyApplied(studyId, memberId)

        val application =
            study.receiveApplication(
                idGenerator.nextId(),
                memberId,
                request.message
            )
        studyMemberRepository.save(application)
    }

    private fun validateIsAlreadyApplied(
        studyId: Long,
        memberId: Long
    ) {
        if (isAlreadyAppliedThisStudy(studyId, memberId)) {
            throw GeneralException(ErrorStatus.STUDY_ALREADY_APPLIED)
        }
    }

    private fun isAlreadyAppliedThisStudy(
        studyId: Long,
        memberId: Long
    ): Boolean =
        studyMemberRepository.existsByStudyIdAndMemberIdAndStudyMemberStatusIn(
            studyId,
            memberId,
            ACTIVE_APPLICATION_STATUSES
        )

    companion object {
        private val ACTIVE_APPLICATION_STATUSES =
            listOf(
                StudyMemberStatus.APPLIED,
                StudyMemberStatus.AWAITING_SELF_APPROVAL,
                StudyMemberStatus.APPROVED
            )
    }
}
