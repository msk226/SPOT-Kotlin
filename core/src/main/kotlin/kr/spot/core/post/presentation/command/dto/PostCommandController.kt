package kr.spot.core.post.presentation.command.dto

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.common.api.status.SuccessStatus
import kr.spot.core.post.application.PostCommandService
import kr.spot.core.post.presentation.command.dto.request.ManagePostRequest
import kr.spot.core.post.presentation.command.dto.response.CreatePostResponse
import org.springframework.web.bind.annotation.*

@Tag(name = "게시글")
@RestController
@RequestMapping("/api/posts")
class PostCommandController(
    private val postCommandService: PostCommandService
) {
    @Operation(summary = "게시글 생성")
    @PostMapping
    fun createPost(
        @RequestBody request: ManagePostRequest,
        @RequestHeader memberId: Long
    ): ApiResponse<CreatePostResponse> {
        val postId =
            postCommandService.createPost(
                memberId = memberId,
                title = request.title,
                content = request.content,
                postType = request.postType
            )
        return ApiResponse.created(CreatePostResponse(postId))
    }

    @Operation(summary = "게시글 수정")
    @PutMapping("/{postId}")
    fun updatePost(
        @RequestBody request: ManagePostRequest,
        @PathVariable postId: Long,
        @RequestHeader memberId: Long
    ): ApiResponse<Unit> {
        postCommandService.updatePost(
            memberId = memberId,
            postId = postId,
            title = request.title,
            content = request.content,
            postType = request.postType
        )
        return ApiResponse.success(SuccessStatus.NO_CONTENT)
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{postId}")
    fun deletePost(
        @PathVariable postId: Long,
        @RequestHeader memberId: Long
    ): ApiResponse<Unit> {
        postCommandService.deletePost(memberId = memberId, postId = postId)
        return ApiResponse.success(SuccessStatus.NO_CONTENT)
    }

    @Operation(summary = "게시글 좋아요")
    @PostMapping("/{postId}/like")
    fun likePost(
        @PathVariable postId: Long,
        @RequestHeader memberId: Long
    ): ApiResponse<Unit> {
        postCommandService.likePost(memberId = memberId, postId = postId)
        return ApiResponse.success(SuccessStatus.NO_CONTENT)
    }

    @Operation(summary = "게시글 좋아요 취소")
    @DeleteMapping("/{postId}/like")
    fun unlikePost(
        @PathVariable postId: Long,
        @RequestHeader memberId: Long
    ): ApiResponse<Unit> {
        postCommandService.unlikePost(memberId = memberId, postId = postId)
        return ApiResponse.success(SuccessStatus.NO_CONTENT)
    }
}
