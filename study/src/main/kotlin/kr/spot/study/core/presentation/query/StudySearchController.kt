package kr.spot.study.core.presentation.query

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import kr.spot.common.api.ApiResponse
import kr.spot.study.core.application.query.GetMyStudyInfoService
import kr.spot.study.core.domain.enums.Category
import kr.spot.study.core.domain.enums.FeeCategory
import kr.spot.study.core.domain.enums.SortBy
import kr.spot.study.core.domain.enums.StudyMemberStatus
import kr.spot.study.core.presentation.query.dto.response.GetStudyOverviewResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "스터디 조회")
@RestController
@RequestMapping("/api/studies")
@Suppress("TooManyFunctions", "LongParameterList")
class StudySearchController(
    private val getMyStudyInfoService: GetMyStudyInfoService
) {

    @Operation(
        summary = "모집 중 스터디 조회",
        description = "모집 중인 스터디를 조회합니다. 카테고리, 정렬 방식 등을 필터링할 수 있습니다."
    )
    @GetMapping("/recruiting")
    fun getRecruitingStudies(
        @RequestHeader viewerId: Long,
        @RequestParam(required = false) feeCategory: FeeCategory?,
        @RequestParam(required = false) categories: List<Category>?,
        @RequestParam(required = false) isOnline: Boolean?,
        @RequestParam(required = false) sortBy: SortBy?,
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "10") @Min(1) @Max(50) size: Int
    ): ResponseEntity<ApiResponse<GetStudyOverviewResponse>> =
        ResponseEntity.ok(
            ApiResponse.ok(
                getMyStudyInfoService.getRecruitingStudies(
                    viewerId,
                    feeCategory,
                    categories,
                    isOnline,
                    sortBy,
                    cursor,
                    size
                )
            )
        )
}
