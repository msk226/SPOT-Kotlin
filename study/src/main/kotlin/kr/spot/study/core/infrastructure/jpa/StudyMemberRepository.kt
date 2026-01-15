package kr.spot.study.core.infrastructure.jpa

import kr.spot.common.api.exception.GeneralException
import kr.spot.common.api.status.ErrorStatus
import kr.spot.study.core.domain.StudyMember
import kr.spot.study.core.domain.enums.StudyMemberStatus
import org.springframework.data.jpa.repository.JpaRepository

interface StudyMemberRepository :
    JpaRepository<StudyMember, Long>,
    StudyMemberRepositoryCustom {
    fun getStudyMemberById(studyMemberId: Long): StudyMember =
        findById(studyMemberId)
            .orElseThrow { GeneralException(ErrorStatus.STUDY_MEMBER_NOT_FOUND) }

    fun existsByStudyIdAndMemberIdAndStudyMemberStatusIn(
        studyId: Long,
        memberId: Long,
        studyMemberStatuses: List<StudyMemberStatus>
    ): Boolean

    fun findAllByStudyIdAndStudyMemberStatusIn(
        studyId: Long,
        studyMemberStatuses: List<StudyMemberStatus>
    ): List<StudyMember>

    fun deleteByMemberId(memberId: Long)
}
