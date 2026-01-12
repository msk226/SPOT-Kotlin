package kr.spot.core.member.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.common.api.status.SuccessStatus
import kr.spot.core.member.application.MemberCommandService
import kr.spot.core.member.application.MemberQueryService
import kr.spot.core.member.presentation.dto.request.*
import kr.spot.core.member.presentation.dto.response.*
import org.springframework.web.bind.annotation.*

@Tag(name = "회원")
@RestController
@RequestMapping("/api/members")
class MemberController(
    private val memberCommandService: MemberCommandService,
    private val memberQueryService: MemberQueryService
) {
    // ==================== Query ====================

    @Operation(summary = "회원 이름 조회")
    @GetMapping("/name")
    fun getMemberName(
        @RequestAttribute("memberId") memberId: Long
    ): ApiResponse<GetMemberNameResponse> {
        val name = memberQueryService.getMemberName(memberId)
        return ApiResponse.ok(GetMemberNameResponse.from(name))
    }

    @Operation(summary = "회원 정보 조회")
    @GetMapping("/info")
    fun getMemberInfo(
        @RequestAttribute("memberId") memberId: Long
    ): ApiResponse<GetMemberInfoResponse> {
        val member = memberQueryService.getMember(memberId)
        return ApiResponse.ok(GetMemberInfoResponse.from(member))
    }

    @Operation(summary = "선호 카테고리 조회")
    @GetMapping("/prefer-categories")
    fun getPreferCategories(
        @RequestAttribute("memberId") memberId: Long
    ): ApiResponse<GetMemberPreferCategoryResponse> {
        val categories = memberQueryService.getPreferredCategories(memberId)
        return ApiResponse.ok(GetMemberPreferCategoryResponse.from(categories))
    }

    @Operation(summary = "선호 지역 조회")
    @GetMapping("/prefer-regions")
    fun getPreferRegions(
        @RequestAttribute("memberId") memberId: Long
    ): ApiResponse<GetMemberPreferRegionResponse> {
        val regions = memberQueryService.getPreferredRegions(memberId)
        return ApiResponse.ok(GetMemberPreferRegionResponse.from(regions))
    }

    // ==================== Command ====================

    @Operation(summary = "회원 선호 카테고리 설정")
    @PostMapping("/prefer-categories")
    fun registerPreferCategories(
        @RequestBody request: RegisterPreferredCategoryRequest,
        @RequestAttribute ("memberId") memberId: Long
    ): ApiResponse<Unit> {
        memberCommandService.registerPreferCategories(memberId, request.categories)
        return ApiResponse.success(SuccessStatus.NO_CONTENT)
    }

    @Operation(summary = "회원 선호 지역 설정")
    @PostMapping("/prefer-regions")
    fun registerPreferRegions(
        @RequestBody request: RegisterPreferredRegionRequest,
        @RequestAttribute ("memberId") memberId: Long
    ): ApiResponse<Unit> {
        memberCommandService.registerPreferRegions(memberId, request.regionCodes)
        return ApiResponse.success(SuccessStatus.NO_CONTENT)
    }

    @Operation(summary = "회원 이름 수정")
    @PostMapping("/name")
    fun updateMemberName(
        @RequestBody request: UpdateMemberNameRequest,
        @RequestAttribute("memberId") memberId: Long
    ): ApiResponse<Unit> {
        memberCommandService.updateName(memberId, request.name)
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

    // ==================== Test ====================

    @Operation(summary = "[테스트용] 회원 생성", description = "개발/테스트 환경에서 사용하는 회원 생성 API")
    @PostMapping("/test")
    fun createTestMember(
        @RequestBody request: CreateTestMemberRequest
    ): ApiResponse<CreateTestMemberResponse> {
        val memberId = memberCommandService.createTestMember(request.name, request.email)
        return ApiResponse.created(CreateTestMemberResponse(memberId))
    }
}
