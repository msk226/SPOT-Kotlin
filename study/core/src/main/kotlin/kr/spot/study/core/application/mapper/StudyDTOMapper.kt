package kr.spot.study.core.application.mapper

import kr.spot.study.core.domain.Study
import kr.spot.study.core.presentation.query.dto.response.GetStudyOverviewResponse
import kr.spot.study.core.presentation.query.dto.response.GetStudyOverviewResponse.StudyOverview

object StudyDTOMapper {
    fun toDTO(
        studies: List<Study>,
        likedStudyIds: Set<Long>?,
        hasNext: Boolean,
        nextCursor: Long?,
        totalElements: Long?
    ): GetStudyOverviewResponse {
        val safeLikedIds = likedStudyIds ?: emptySet()

        val list =
            studies.map { study ->
                StudyOverview.of(
                    id = study.id,
                    name = study.name,
                    description = study.description,
                    maxMembers = study.maxMember,
                    currentMembers = study.currentMember,
                    isLiked = safeLikedIds.contains(study.id),
                    likeCount = study.likeCount,
                    hitCount = study.viewCount,
                    imageUrl = study.imageUrl
                )
            }

        return GetStudyOverviewResponse.of(list, hasNext, nextCursor, totalElements)
    }

    fun toDTO(
        studies: List<Study>,
        hasNext: Boolean,
        nextCursor: Long?,
        totalElements: Long?
    ): GetStudyOverviewResponse = toDTO(studies, emptySet(), hasNext, nextCursor, totalElements)
}
