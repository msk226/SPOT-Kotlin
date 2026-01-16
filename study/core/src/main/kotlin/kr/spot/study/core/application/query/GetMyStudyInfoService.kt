package kr.spot.study.core.application.query

import kr.spot.study.core.application.mapper.StudyDTOMapper
import kr.spot.study.core.domain.Study
import kr.spot.study.core.domain.enums.Category
import kr.spot.study.core.domain.enums.FeeCategory
import kr.spot.study.core.domain.enums.SortBy
import kr.spot.study.core.infrastructure.jpa.StudyLikeRepository
import kr.spot.study.core.infrastructure.jpa.querydsl.StudyQueryRepository
import kr.spot.study.core.presentation.query.dto.response.GetStudyOverviewResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
@Suppress("TooManyFunctions")
class GetMyStudyInfoService(
    private val studyQueryRepository: StudyQueryRepository,
    private val studyLikeRepository: StudyLikeRepository
) {
    companion object {
        const val MAX_PAGE_SIZE = 50
    }

    @Suppress("LongParameterList")
    fun getRecruitingStudies(
        viewerId: Long,
        feeCategory: FeeCategory?,
        categories: List<Category>?,
        isOnline: Boolean?,
        sortBy: SortBy?,
        cursor: Long?,
        size: Int
    ): GetStudyOverviewResponse {
        val pageSize = minOf(size, MAX_PAGE_SIZE)

        val rows =
            studyQueryRepository.findRecruitingStudies(
                feeCategory,
                categories,
                isOnline,
                sortBy,
                cursor,
                pageSize + 1
            )

        val totalElements =
            studyQueryRepository.countRecruitingStudies(
                feeCategory,
                categories,
                isOnline
            )
        return toCursorPage(rows, viewerId, pageSize, totalElements)
    }

    private fun toCursorPage(
        rows: List<Study>,
        viewerId: Long,
        pageSize: Int,
        totalElements: Long
    ): GetStudyOverviewResponse {
        val hasNext = rows.size > pageSize
        val pageContent = if (hasNext) rows.subList(0, pageSize) else rows
        val nextCursor = if (hasNext) pageContent.last().id else null
        val likedStudyIds = studyLikeRepository.findStudyIdsByMemberId(viewerId)
        return StudyDTOMapper.toDTO(pageContent, likedStudyIds, hasNext, nextCursor, totalElements)
    }
}
