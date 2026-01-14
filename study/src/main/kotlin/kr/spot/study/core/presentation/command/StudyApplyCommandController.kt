package kr.spot.study.core.presentation.command

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.study.core.application.command.ApplyStudyService
import kr.spot.study.core.domain.enums.Decision
import kr.spot.study.core.presentation.command.dto.request.ApplyStudyRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "스터디 신청", description = "스터디 신청 관련 API")
@RestController
@RequestMapping("/api/studies")
class StudyApplyCommandController(
    private val applyStudyService: ApplyStudyService
) {
    @Operation(summary = "스터디 신청", description = "특정 스터디에 참여 신청을 합니다.")
    @PostMapping("/{studyId}/apply")
    fun applyStudy(
        @PathVariable studyId: Long,
        @Parameter(hidden = true) @RequestHeader memberId: Long,
        @RequestBody request: ApplyStudyRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        applyStudyService.applyStudy(studyId, memberId, request)
        return ResponseEntity.ok(ApiResponse.ok())
    }

    @Operation(summary = "스터디 신청 승인/거절", description = "스터디 신청을 승인하거나 거절합니다.")
    @PostMapping("/applications/{applicationId}")
    fun approveStudyApplication(
        @PathVariable applicationId: Long,
        @Parameter(hidden = true) @RequestHeader memberId: Long,
        @RequestParam decision: Decision
    ): ResponseEntity<ApiResponse<Unit>> {
        applyStudyService.processStudyApplication(applicationId, memberId, decision)
        return ResponseEntity.ok(ApiResponse.ok())
    }
}
