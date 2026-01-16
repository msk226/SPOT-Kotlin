package kr.spot.core.member.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.common.api.status.SuccessStatus
import kr.spot.core.member.application.MemberCommandService
import kr.spot.core.member.application.MemberQueryService
import kr.spot.core.member.presentation.dto.request.CreateTestMemberRequest
import kr.spot.core.member.presentation.dto.request.RegisterPreferredCategoryRequest
import kr.spot.core.member.presentation.dto.request.UpdateMemberInfoRequest
import kr.spot.core.member.presentation.dto.response.CreateTestMemberResponse
import kr.spot.core.member.presentation.dto.response.GetMemberInfoResponse
import kr.spot.core.member.presentation.dto.response.GetMemberNameResponse
import org.springframework.web.bind.annotation.*

@Tag(name = "회원")
@RestController
@RequestMapping("/api/members")
class MemberController(
    private val memberCommandService: kr.spot.core.member.application.MemberCommandService,
    private val memberQueryService: kr.spot.core.member.application.MemberQueryService
) {
    // ==================== Query ====================

    @Operation(summary = "회원 이름 조회")
    @GetMapping("/name")
    fun getMemberName(
        @Parameter(hidden = true) @RequestHeader memberId: Long
    ): ApiResponse<kr.spot.core.member.presentation.dto.response.GetMemberNameResponse> {
        val name = memberQueryService.getMemberName(memberId)
        return ApiResponse.ok(
            _root_ide_package_.kr.spot.core.member.presentation.dto.response.GetMemberNameResponse.Companion
                .from(name)
        )
    }

    @Operation(summary = "회원 정보 조회")
    @GetMapping("/info")
    fun getMemberInfo(
        @Parameter(hidden = true) @RequestHeader memberId: Long
    ): ApiResponse<kr.spot.core.member.presentation.dto.response.GetMemberInfoResponse> {
        val member = memberQueryService.getMember(memberId)
        return ApiResponse.ok(
            _root_ide_package_.kr.spot.core.member.presentation.dto.response.GetMemberInfoResponse.Companion.from(
                member
            )
        )
    }

    // ==================== Command ====================

    @Operation(summary = "회원 선호 카테고리 설정")
    @PostMapping("/prefer-categories")
    fun registerPreferCategories(
        @RequestBody request: kr.spot.core.member.presentation.dto.request.RegisterPreferredCategoryRequest,
        @RequestAttribute("memberId") memberId: Long
    ): ApiResponse<Unit> {
        memberCommandService.registerPreferCategories(memberId, request.categories)
        return ApiResponse.success(SuccessStatus.NO_CONTENT)
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/me")
    fun withdraw(
        @RequestAttribute("memberId") memberId: Long
    ): ApiResponse<Unit> {
        memberCommandService.withdraw(memberId)
        return ApiResponse.ok()
    }

    @Operation(summary = "회원 정보 수정")
    @PutMapping("/me")
    fun updateMemberInfo(
        @RequestHeader memberId: Long,
        @RequestBody updateMemberInfoRequest: kr.spot.core.member.presentation.dto.request.UpdateMemberInfoRequest
    ): ApiResponse<Unit> {
        memberCommandService.updateProfile(memberId, updateMemberInfoRequest)
        return ApiResponse.ok()
    }

    // ==================== Test ====================

    @Operation(summary = "[테스트용] 회원 생성", description = "개발/테스트 환경에서 사용하는 회원 생성 API")
    @PostMapping("/test")
    fun createTestMember(
        @RequestBody request: kr.spot.core.member.presentation.dto.request.CreateTestMemberRequest
    ): ApiResponse<kr.spot.core.member.presentation.dto.response.CreateTestMemberResponse> {
        val memberId = memberCommandService.createTestMember(request.name, request.email)
        return ApiResponse.created(
            _root_ide_package_.kr.spot.core.member.presentation.dto.response.CreateTestMemberResponse(
                memberId
            )
        )
    }
}
