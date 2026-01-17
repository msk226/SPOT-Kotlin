package kr.spot.core.attendance.presentation.command

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.core.attendance.application.command.AttendanceCheckCommandService
import kr.spot.core.attendance.presentation.command.dto.AttendanceCheckResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "출석 체크")
@RestController
@RequestMapping("/api/attendances")
class AttendanceCheckCommandController(
    private val attendanceCheckCommandService: AttendanceCheckCommandService
) {
    @Operation(summary = "출석 체크")
    @PostMapping("/check-in")
    fun checkIn(
        @RequestHeader @Parameter(hidden = true) memberId: Long
    ): ApiResponse<AttendanceCheckResult> = ApiResponse.ok(attendanceCheckCommandService.checkIn(memberId))
}
