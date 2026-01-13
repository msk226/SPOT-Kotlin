package kr.spot.study.core.application.query

import kr.spot.study.core.domain.enums.StudyMemberStatus
import kr.spot.study.core.infrastructure.jpa.StudyMemberRepositoryCustom
import kr.spot.study.core.presentation.query.dto.response.GetMyAppliedStudyResponse
import kr.spot.study.core.presentation.query.dto.response.GetMyAppliedStudyResponse.MyAppliedStudy
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetStudyApplicationService(
    @Qualifier("studyMemberRepositoryCustomImpl")
    private val studyMemberRepositoryCustom: StudyMemberRepositoryCustom,
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
}
