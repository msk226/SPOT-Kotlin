package kr.spot.study.core.presentation.query

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.study.core.application.query.GetStudyApplicationService
import kr.spot.study.core.presentation.query.dto.response.GetMyAppliedStudyResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "스터디 신청", description = "스터디 신청 관련 API")
@RestController
@RequestMapping("/api/studies")
class StudyApplyQueryController(
    private val getStudyApplicationService: GetStudyApplicationService
) {
    @Operation(summary = "승인된 스터디 내역 조회", description = "내가 신청해서 승인된 스터디 내역을 조회합니다.")
    @ApiResponses(
        io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = [Content(schema = Schema(implementation = GetMyAppliedStudyResponse::class))]
        )
    )
    @GetMapping("/applied")
    fun getMyAppliedStudies(
        @RequestHeader memberId: Long
    ): ResponseEntity<ApiResponse<GetMyAppliedStudyResponse>> =
        ResponseEntity.ok(
            ApiResponse.ok(
                getStudyApplicationService.getMyAppliedStudy(memberId)
            )
        )
}
