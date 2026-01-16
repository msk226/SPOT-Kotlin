package kr.spot.post.presentation.query

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.post.application.PostQueryService
import kr.spot.post.domain.enums.PostType
import kr.spot.post.presentation.query.dto.response.PostDetailResponse
import kr.spot.post.presentation.query.dto.response.PostListResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "게시글")
@RestController
@RequestMapping("/api/posts")
class PostQueryController(
    private val postQueryService: kr.spot.post.application.PostQueryService
) {
    @Operation(summary = "게시글 상세 조회")
    @GetMapping("/{postId}")
    fun getPostDetail(
        @PathVariable postId: Long,
        @Parameter(hidden = true) @RequestHeader memberId: Long?
    ): ApiResponse<kr.spot.post.presentation.query.dto.response.PostDetailResponse> {
        val response = postQueryService.getPostDetail(postId, memberId)
        return ApiResponse.ok(response)
    }

    @Operation(summary = "게시글 목록 조회")
    @GetMapping("")
    fun getPostList(
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) postType: kr.spot.post.domain.enums.PostType?,
        @Parameter(hidden = true) @RequestHeader memberId: Long?
    ): ApiResponse<kr.spot.post.presentation.query.dto.response.PostListResponse> {
        val response = postQueryService.getPostList(cursor, size, postType, memberId)
        return ApiResponse.ok(response)
    }
}
