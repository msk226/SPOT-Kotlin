package kr.spot.core.member.presentation.query

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.core.member.application.MemberCommandService
import kr.spot.core.member.application.MemberQueryService
import kr.spot.core.member.presentation.query.dto.response.GetMemberInfoResponse
import kr.spot.core.member.presentation.query.dto.response.GetMemberNameResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "회원")
@RestController
@RequestMapping("/api/members")
class MemberQueryController(
    private val memberCommandService: MemberCommandService,
    private val memberQueryService: MemberQueryService
) {
    // ==================== Query ====================

    @Operation(summary = "회원 이름 조회")
    @GetMapping("/name")
    fun getMemberName(
        @Parameter(hidden = true) @RequestHeader memberId: Long
    ): ApiResponse<GetMemberNameResponse> {
        val name = memberQueryService.getMemberName(memberId)
        return ApiResponse.Companion.ok(
            GetMemberNameResponse.Companion
                .from(name)
        )
    }

    @Operation(summary = "회원 정보 조회")
    @GetMapping("/info")
    fun getMemberInfo(
        @Parameter(hidden = true) @RequestHeader memberId: Long
    ): ApiResponse<GetMemberInfoResponse> {
        val member = memberQueryService.getMember(memberId)
        return ApiResponse.Companion.ok(
            GetMemberInfoResponse.Companion.from(
                member
            )
        )
    }

    // ==================== Command ====================
}
