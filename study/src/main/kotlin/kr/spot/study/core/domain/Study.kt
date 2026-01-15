package kr.spot.study.core.domain

import jakarta.persistence.*
import kr.spot.common.api.exception.GeneralException
import kr.spot.common.api.status.ErrorStatus
import kr.spot.study.core.domain.enums.Decision
import kr.spot.study.core.domain.enums.RecruitingStatus
import kr.spot.study.core.domain.enums.StudyMemberStatus
import kr.spot.study.core.domain.vo.Fee
import kr.spot.study.global.domain.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "study")
@SQLDelete(sql = "UPDATE study SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class Study private constructor(
    @Id
    val id: Long,
    val leaderId: Long,
    val name: String,
    val maxMember: Int,
    @Embedded
    val fee: Fee,
    description: String,
    imageUrl: String,
    val isOnline: Boolean
) : BaseEntity() {
    var description: String = description
        private set

    @Enumerated(EnumType.STRING)
    var recruitingStatus: RecruitingStatus = RecruitingStatus.RECRUITING
        private set

    var imageUrl: String = imageUrl
        private set

    var currentMember: Int = CURRENT_MEMBERS
        private set

    val viewCount: Long = 0L

    val likeCount: Long = 0L

    fun processApplication(
        application: StudyMember,
        requesterId: Long,
        decision: Decision
    ) {
        validateIsFull()
        validateIsStudyOwner(requesterId)
        validateIsValidStatusToProcessApply(application)
        application.decide(decision)
        increaseMemberCountIfApproved(decision)
    }

    private fun validateIsFull() {
        if (currentMember >= maxMember) {
            throw GeneralException(ErrorStatus.STUDY_IS_FULL)
        }
    }

    private fun increaseMemberCountIfApproved(decision: Decision) {
        if (decision == Decision.APPROVE) {
            currentMember += 1
        }
    }

    fun validateIsStudyOwner(requesterId: Long) {
        if (leaderId != requesterId) {
            throw GeneralException(ErrorStatus.ONLY_LEADER_CAN_ACCESS)
        }
    }

    private fun validateIsValidStatusToProcessApply(application: StudyMember) {
        if (application.studyMemberStatus != StudyMemberStatus.APPLIED) {
            throw GeneralException(ErrorStatus.NOT_PENDING_APPLICATION)
        }
    }

    fun receiveApplication(
        id: Long,
        memberId: Long,
        message: String
    ): StudyMember = StudyMember.apply(id, this.id, memberId, message)

    companion object {
        private const val CURRENT_MEMBERS = 1

        fun of(
            id: Long,
            leaderId: Long,
            name: String,
            maxMembers: Int,
            fee: Fee,
            description: String
        ): Study = of(id, leaderId, name, maxMembers, fee, null, description, false)

        fun of(
            id: Long,
            leaderId: Long,
            name: String,
            maxMembers: Int,
            fee: Fee,
            imageUrl: String?,
            description: String
        ): Study = of(id, leaderId, name, maxMembers, fee, imageUrl, description, false)

        @Suppress("LongParameterList")
        fun of(
            id: Long,
            leaderId: Long,
            name: String,
            maxMembers: Int,
            fee: Fee,
            imageUrl: String?,
            description: String,
            isOnline: Boolean?
        ): Study {
            validateStudyNameIsNotBlank(name)
            validateMaxMembers(maxMembers)
            return Study(
                id = id,
                leaderId = leaderId,
                name = name,
                maxMember = maxMembers,
                fee = fee,
                imageUrl = imageUrl ?: "",
                description = description,
                isOnline = (isOnline == true)
            )
        }

        private fun validateStudyNameIsNotBlank(name: String) {
            if (name.isBlank()) {
                throw GeneralException(ErrorStatus.NAME_CAN_NOT_NULL_OR_EMPTY)
            }
        }

        private fun validateMaxMembers(maxMembers: Int) {
            if (maxMembers <= 0) {
                throw GeneralException(ErrorStatus.MAX_MEMBERS_MUST_BE_POSITIVE)
            }
        }
    }
}
