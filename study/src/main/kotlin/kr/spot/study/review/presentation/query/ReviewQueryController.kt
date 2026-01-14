package kr.spot.study.review.presentation.query

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import kr.spot.common.api.ApiResponse
import kr.spot.study.review.application.query.GetReviewService
import kr.spot.study.review.presentation.query.dto.GetReviewListResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "스터디 회고록")
@RestController
@RequestMapping("/api/studies/{studyId}/reviews")
class ReviewQueryController(
    private val getReviewService: GetReviewService
) {
    @Operation(
        summary = "스터디 회고록 목록 조회",
        description = "특정 스터디의 회고록 목록을 조회합니다. 커서 기반 페이지네이션을 지원합니다."
    )
    @GetMapping
    fun getReviewList(
        @PathVariable studyId: Long,
        @Parameter(hidden = true) @RequestHeader memberId: Long,
        @RequestParam(required = false) cursor: Long?,
        @Parameter(description = "페이지 크기 (1~50)")
        @RequestParam(defaultValue = "10")
        @Min(1)
        @Max(50) size: Int
    ): ResponseEntity<ApiResponse<GetReviewListResponse>> {
        val response = getReviewService.getReviewList(studyId, memberId, cursor, size)
        return ResponseEntity.ok(ApiResponse.ok(response))
    }
}
