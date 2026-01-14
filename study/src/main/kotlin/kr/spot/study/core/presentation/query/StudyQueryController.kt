package kr.spot.study.core.presentation.query

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.study.core.application.query.GetStudyDetailService
import kr.spot.study.core.presentation.query.dto.response.GetStudyInfoResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "스터디 조회")
@RestController
@RequestMapping("/api/studies/{studyId}")
class StudyQueryController(
    private val getStudyDetailService: GetStudyDetailService
) {
    @Operation(
        summary = "스터디 상세 정보 조회",
        description = "스터디의 상세 정보를 조회합니다."
    )
    @GetMapping("/info")
    fun getStudyInfo(
        @PathVariable studyId: Long,
        @RequestHeader viewerId: Long
    ): ResponseEntity<ApiResponse<GetStudyInfoResponse>> =
        ResponseEntity.ok(
            ApiResponse.ok(
                getStudyDetailService.getStudyInfo(studyId, viewerId)
            )
        )
}
