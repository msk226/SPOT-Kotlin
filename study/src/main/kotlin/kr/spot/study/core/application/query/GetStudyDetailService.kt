package kr.spot.study.core.application.query

import kr.spot.common.ports.GetMemberInfoPort
import kr.spot.common.ports.dto.MemberInfoResponse
import kr.spot.study.core.domain.Study
import kr.spot.study.core.domain.StudyMember
import kr.spot.study.core.domain.association.StudyCategory
import kr.spot.study.core.domain.enums.Category
import kr.spot.study.core.domain.enums.StudyMemberStatus
import kr.spot.study.core.infrastructure.jpa.StudyCategoryRepository
import kr.spot.study.core.infrastructure.jpa.StudyMemberRepository
import kr.spot.study.core.infrastructure.jpa.StudyRepository
import kr.spot.study.core.presentation.query.dto.response.GetStudyInfoResponse
import kr.spot.study.core.presentation.query.dto.response.GetStudyInfoResponse.Statistics
import kr.spot.study.core.presentation.query.dto.response.GetStudyMembersResponse
import kr.spot.study.core.presentation.query.dto.response.GetStudyMembersResponse.MemberResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetStudyDetailService(
    private val studyRepository: StudyRepository,
    private val studyCategoryRepository: StudyCategoryRepository,
    private val studyMemberRepository: StudyMemberRepository,
    private val studyViewCountService: StudyViewCountService,
    private val getMemberInfoPort: GetMemberInfoPort
) {
    companion object {
        private val ACTIVE_MEMBER_STATUSES =
            listOf(
                StudyMemberStatus.OWNER,
                StudyMemberStatus.APPROVED
            )
    }

    fun getStudyInfo(
        studyId: Long,
        viewerId: Long
    ): GetStudyInfoResponse {
        val study = findStudy(studyId)
        val categories = findCategories(studyId)
        val statistics = buildStatistics(study, studyId, viewerId)

        return toStudyInfoResponse(study, categories, statistics)
    }

    fun getStudyMembers(studyId: Long): GetStudyMembersResponse {
        val studyMembers = findActiveStudyMembers(studyId)
        val memberInfoMap = fetchMemberInfos(studyMembers)
        val members = toMemberResponses(studyMembers, memberInfoMap)

        return GetStudyMembersResponse.of(members, members.size.toLong())
    }

    private fun findStudy(studyId: Long): Study = studyRepository.getStudyById(studyId)

    private fun findCategories(studyId: Long): List<Category> =
        studyCategoryRepository
            .findAllByStudyId(studyId)
            .map(StudyCategory::category)

    private fun buildStatistics(
        study: Study,
        studyId: Long,
        viewerId: Long
    ): Statistics {
        val displayViewCount = studyViewCountService.calculateDisplayViewCount(study, studyId, viewerId)
        return Statistics.of(
            study.maxMember,
            study.currentMember,
            study.likeCount,
            displayViewCount
        )
    }

    private fun toStudyInfoResponse(
        study: Study,
        categories: List<Category>,
        statistics: Statistics
    ): GetStudyInfoResponse =
        GetStudyInfoResponse.of(
            study.id,
            study.name,
            study.description,
            study.imageUrl,
            categories,
            statistics
        )

    private fun findActiveStudyMembers(studyId: Long): List<StudyMember> =
        studyMemberRepository.findAllByStudyIdAndStudyMemberStatusIn(studyId, ACTIVE_MEMBER_STATUSES)

    private fun fetchMemberInfos(studyMembers: List<StudyMember>): Map<Long, MemberInfoResponse> {
        val memberIds = studyMembers.map(StudyMember::memberId)
        return getMemberInfoPort.getMemberInfo(memberIds)
    }

    private fun toMemberResponses(
        studyMembers: List<StudyMember>,
        memberInfoMap: Map<Long, MemberInfoResponse>
    ): List<MemberResponse> = studyMembers.map { toMemberResponse(it, memberInfoMap) }

    private fun toMemberResponse(
        studyMember: StudyMember,
        memberInfoMap: Map<Long, MemberInfoResponse>
    ): MemberResponse {
        val memberInfo = memberInfoMap[studyMember.memberId]!!
        val isOwner = studyMember.studyMemberStatus == StudyMemberStatus.OWNER

        return MemberResponse.of(
            studyMember.memberId,
            memberInfo.name,
            memberInfo.profileImageUrl,
            isOwner
        )
    }
}
