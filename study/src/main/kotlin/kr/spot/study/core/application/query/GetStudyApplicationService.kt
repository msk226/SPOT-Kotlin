package kr.spot.study.core.application.query

import kr.spot.common.ports.GetMemberInfoPort
import kr.spot.study.core.application.validator.StudyAccessValidator
import kr.spot.study.core.domain.enums.StudyMemberStatus
import kr.spot.study.core.infrastructure.jpa.StudyMemberRepositoryCustom
import kr.spot.study.core.presentation.query.dto.response.GetAppliesResponse
import kr.spot.study.core.presentation.query.dto.response.GetAppliesResponse.Apply
import kr.spot.study.core.presentation.query.dto.response.GetMyAppliedStudyResponse
import kr.spot.study.core.presentation.query.dto.response.GetMyAppliedStudyResponse.MyAppliedStudy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetStudyApplicationService(
    private val getMemberInfoPort: GetMemberInfoPort,
    private val studyMemberRepositoryCustom: StudyMemberRepositoryCustom,
    private val studyAccessValidator: StudyAccessValidator
) {
    fun getMyAppliedStudy(memberId: Long): GetMyAppliedStudyResponse {
        val applications =
            studyMemberRepositoryCustom.findMyAppliedStudiesWithStudyInfo(
                memberId,
                StudyMemberStatus.AWAITING_SELF_APPROVAL
            )

        return GetMyAppliedStudyResponse.of(
            applications.map { info ->
                MyAppliedStudy(
                    info.studyMemberId,
                    info.studyId,
                    info.studyName,
                    info.studyProfileImageUrl
                )
            }
        )
    }

    @Deprecated("Use approveStudyApplication instead", ReplaceWith(""))
    fun getStudyApplications(
        studyId: Long,
        requesterId: Long
    ): GetAppliesResponse {
        studyAccessValidator.validateStudyLeader(studyId, requesterId)

        val applications =
            studyMemberRepositoryCustom.findApplicationsByStudyIdAndStatus(
                studyId,
                StudyMemberStatus.APPLIED
            )

        if (applications.isEmpty()) {
            return GetAppliesResponse.of(emptyList())
        }

        val applicantIds = applications.map { it.memberId }.distinct()
        val memberInfoMap = getMemberInfoPort.getMemberInfo(applicantIds)

        val applies =
            applications.map { application ->
                val memberInfo = memberInfoMap[application.memberId]!!
                Apply.of(
                    application.id,
                    application.memberId,
                    memberInfo.name,
                    application.message,
                    memberInfo.profileImageUrl
                )
            }

        return GetAppliesResponse.of(applies)
    }
}
