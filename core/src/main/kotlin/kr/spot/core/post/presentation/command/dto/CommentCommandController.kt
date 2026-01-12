package kr.spot.core.post.presentation.command.dto

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.common.api.status.SuccessStatus
import kr.spot.core.post.application.CommentCommandService
import kr.spot.core.post.presentation.command.dto.request.ManageCommentRequest
import kr.spot.core.post.presentation.command.dto.response.CreateCommentResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "게시글 - 댓글")
@RestController
@RequestMapping("/api/posts")
class CommentCommandController(
    private val commentCommandService: CommentCommandService
) {
    @Operation(summary = "댓글 생성")
    @PostMapping("{postId}/comments")
    fun createComment(
        @RequestBody request: ManageCommentRequest,
        @PathVariable postId: Long,
        @RequestAttribute("memberId") memberId: Long
    ): ApiResponse<CreateCommentResponse> {
        val commentId =
            commentCommandService.createComment(
                memberId = memberId,
                postId = postId,
                content = request.content
            )
        return ApiResponse.created(CreateCommentResponse(commentId))
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/{postId}/comments/{commentId}")
    fun updateComment(
        @RequestBody request: ManageCommentRequest,
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
        @RequestAttribute("memberId") memberId: Long
    ): ApiResponse<Unit> {
        commentCommandService.updateComment(
            memberId = memberId,
            commentId = commentId,
            postId = postId,
            content = request.content
        )
        return ApiResponse.success(SuccessStatus.NO_CONTENT)
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{postId}/comments/{commentId}")
    fun deleteComment(
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
        @RequestAttribute("memberId") memberId: Long
    ): ApiResponse<Unit> {
        commentCommandService.deleteComment(memberId = memberId, postId = postId, commentId = commentId)
        return ApiResponse.success(SuccessStatus.NO_CONTENT)
    }
}
