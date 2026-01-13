package kr.spot.study.core.infrastructure.jpa

import kr.spot.study.core.domain.StudyMember
import kr.spot.study.core.domain.enums.StudyMemberStatus
import kr.spot.study.core.infrastructure.jpa.dto.StudyApplicationInfo

interface StudyMemberRepositoryCustom {
    fun findMyAppliedStudiesWithStudyInfo(
        memberId: Long,
        status: StudyMemberStatus
    ): List<StudyApplicationInfo>

    fun findApplicationsByStudyIdAndStatus(
        studyId: Long,
        status: StudyMemberStatus
    ): List<StudyMember>
}
