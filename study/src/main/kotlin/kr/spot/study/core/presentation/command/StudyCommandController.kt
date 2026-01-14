package kr.spot.study.core.presentation.command

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.study.core.application.command.CreateStudyService
import kr.spot.study.core.application.command.StudyLikeService
import kr.spot.study.core.presentation.command.dto.request.CreateStudyRequest
import kr.spot.study.core.presentation.command.dto.response.CreateStudyResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController

@Tag(name = "스터디")
@RestController
@RequestMapping("/api/studies")
class StudyCommandController(
    private val createStudyService: CreateStudyService,
    private val studyLikeService: StudyLikeService
) {
    @Operation(summary = "스터디 생성", description = "새로운 스터디를 생성합니다.")
    @PostMapping
    fun createStudy(
        @RequestBody request: CreateStudyRequest,
        @RequestHeader memberId: Long
    ): ResponseEntity<ApiResponse<CreateStudyResponse>> {
        val studyId = createStudyService.createStudy(request, memberId)
        return ResponseEntity.ok(
            ApiResponse.created(CreateStudyResponse.from(studyId))
        )
    }

    @Operation(summary = "스터디 좋아요", description = "스터디에 좋아요를 누릅니다.")
    @PostMapping("/{studyId}/like")
    fun likeStudy(
        @PathVariable studyId: Long,
        @RequestHeader memberId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        studyLikeService.likeStudy(studyId, memberId)
        return ResponseEntity.ok(ApiResponse.ok())
    }

    @Operation(summary = "스터디 좋아요 취소", description = "스터디 좋아요를 취소합니다.")
    @DeleteMapping("/{studyId}/like")
    fun unlikeStudy(
        @PathVariable studyId: Long,
        @RequestHeader memberId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        studyLikeService.unlikeStudy(studyId, memberId)
        return ResponseEntity.ok(ApiResponse.ok())
    }
}
