package kr.spot.study.core.domain

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.spot.common.api.exception.GeneralException
import kr.spot.common.api.status.ErrorStatus
import kr.spot.study.core.domain.enums.Decision
import kr.spot.study.core.domain.enums.StudyMemberStatus
import kr.spot.study.global.domain.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "study_member")
@SQLDelete(sql = "UPDATE study_member SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class StudyMember private constructor(
    @Id
    val id: Long,
    val studyId: Long,
    val memberId: Long,
    val message: String?,
    studyMemberStatus: StudyMemberStatus
) : BaseEntity() {
    @Enumerated(EnumType.STRING)
    var studyMemberStatus: StudyMemberStatus = studyMemberStatus
        private set

    fun decide(decision: Decision) {
        when (decision) {
            Decision.APPROVE -> this.studyMemberStatus = StudyMemberStatus.APPROVED
            Decision.REJECT -> this.studyMemberStatus = StudyMemberStatus.REJECTED
        }
    }

    fun decideFinalByApplicant(
        requesterId: Long,
        decision: Decision
    ) {
        if (this.memberId != requesterId) {
            throw GeneralException(ErrorStatus.ONLY_APPLICANT_CAN_SELF_APPROVE)
        }

        if (this.studyMemberStatus != StudyMemberStatus.AWAITING_SELF_APPROVAL) {
            throw GeneralException(ErrorStatus.INVALID_STUDY_MEMBER_STATUS_FOR_SELF_APPROVAL)
        }

        when (decision) {
            Decision.APPROVE -> this.studyMemberStatus = StudyMemberStatus.APPROVED
            Decision.REJECT -> this.studyMemberStatus = StudyMemberStatus.SELF_REJECTED
        }
    }

    companion object {
        fun create(
            id: Long,
            studyId: Long,
            memberId: Long
        ): StudyMember = StudyMember(id, studyId, memberId, null, StudyMemberStatus.OWNER)

        fun apply(
            id: Long,
            studyId: Long,
            memberId: Long,
            message: String
        ): StudyMember = StudyMember(id, studyId, memberId, message, StudyMemberStatus.APPLIED)
    }
}
