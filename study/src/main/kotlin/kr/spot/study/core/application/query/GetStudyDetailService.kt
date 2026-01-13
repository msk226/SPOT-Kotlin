package kr.spot.study.core.application.query

import kr.spot.study.core.domain.Study
import kr.spot.study.core.domain.association.StudyCategory
import kr.spot.study.core.domain.enums.Category
import kr.spot.study.core.infrastructure.jpa.StudyCategoryRepository
import kr.spot.study.core.infrastructure.jpa.StudyRepository
import kr.spot.study.core.presentation.query.dto.response.GetStudyInfoResponse
import kr.spot.study.core.presentation.query.dto.response.GetStudyInfoResponse.Statistics
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetStudyDetailService(
    private val studyRepository: StudyRepository,
    private val studyCategoryRepository: StudyCategoryRepository,
    private val studyViewCountService: StudyViewCountService,
) {
    fun getStudyInfo(
        studyId: Long,
        viewerId: Long
    ): GetStudyInfoResponse {
        val study = findStudy(studyId)
        val categories = findCategories(studyId)
        val statistics = buildStatistics(study, studyId, viewerId)

        return toStudyInfoResponse(study, categories, statistics)
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
}
