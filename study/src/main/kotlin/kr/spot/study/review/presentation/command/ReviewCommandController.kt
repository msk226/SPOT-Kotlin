package kr.spot.study.review.presentation.command

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.common.api.status.SuccessStatus
import kr.spot.study.review.application.command.ManageReviewReactionService
import kr.spot.study.review.application.command.ManageReviewService
import kr.spot.study.review.domain.enums.Reaction
import kr.spot.study.review.presentation.command.dto.CreateReviewRequest
import kr.spot.study.review.presentation.command.dto.CreateReviewResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Tag(name = "스터디 회고록")
@RestController
@RequestMapping("/api/studies/{studyId}/reviews")
class ReviewCommandController(
    private val manageReviewService: ManageReviewService,
    private val manageReviewReactionService: ManageReviewReactionService
) {
    @Operation(summary = "스터디 회고록 작성", description = "특정 스터디에 대한 회고록을 작성합니다.")
    @PostMapping
    fun createReview(
        @PathVariable studyId: Long,
        @RequestHeader("X-Member-Id") memberId: Long,
        @RequestPart request: CreateReviewRequest,
        @RequestPart(required = false) imageFile: MultipartFile?
    ): ResponseEntity<ApiResponse<CreateReviewResponse>> {
        val reviewId = manageReviewService.createReview(studyId, memberId, request, imageFile)
        return ResponseEntity.ok(
            ApiResponse.created(CreateReviewResponse.from(reviewId))
        )
    }

    @Operation(summary = "스터디 회고록 삭제", description = "특정 스터디에 대한 회고록을 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    fun deleteReview(
        @PathVariable studyId: Long,
        @PathVariable reviewId: Long,
        @RequestHeader("X-Member-Id") memberId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        manageReviewService.deleteReview(studyId, reviewId, memberId)
        return ResponseEntity.ok(ApiResponse.success(SuccessStatus.NO_CONTENT))
    }

    @Operation(summary = "스터디 회고록 반응 추가", description = "특정 스터디 회고록에 대한 반응을 추가합니다.")
    @PostMapping("/{reviewId}/reactions")
    fun addReaction(
        @PathVariable studyId: Long,
        @PathVariable reviewId: Long,
        @RequestHeader("X-Member-Id") memberId: Long,
        @RequestParam reaction: Reaction
    ): ResponseEntity<ApiResponse<Unit>> {
        manageReviewReactionService.addReaction(studyId, reviewId, memberId, reaction)
        return ResponseEntity.ok(ApiResponse.created())
    }

    @Operation(summary = "스터디 회고록 반응 제거", description = "특정 스터디 회고록에 대한 반응을 제거합니다.")
    @DeleteMapping("/{reviewId}/reactions")
    fun removeReaction(
        @PathVariable studyId: Long,
        @PathVariable reviewId: Long,
        @RequestHeader("X-Member-Id") memberId: Long,
        @RequestParam reaction: Reaction
    ): ResponseEntity<ApiResponse<Unit>> {
        manageReviewReactionService.removeReaction(studyId, reviewId, memberId, reaction)
        return ResponseEntity.ok(ApiResponse.success(SuccessStatus.NO_CONTENT))
    }
}
