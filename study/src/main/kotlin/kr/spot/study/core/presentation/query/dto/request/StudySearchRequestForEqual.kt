package kr.spot.study.core.presentation.query.dto.request

import kr.spot.study.core.domain.enums.Category
import kr.spot.study.core.domain.enums.FeeCategory
import kr.spot.study.core.domain.enums.RecruitingStatus

data class StudySearchRequestForEqual(
    val feeCategory: FeeCategory?,
    val recruitingStatus: RecruitingStatus?,
    val categories: List<Category>?,
    val cursor: Long?,
    val limit: Int
)
