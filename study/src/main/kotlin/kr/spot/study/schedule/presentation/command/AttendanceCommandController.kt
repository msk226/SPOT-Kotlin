package kr.spot.study.schedule.presentation.command

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.study.schedule.application.command.AttendanceCommandService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "스터디 일정 - 출석체크", description = "스터디 일정 출석체크 관련 API")
@RestController
@RequestMapping("/api/studies/{studyId}/schedules/{scheduleId}/attendance")
class AttendanceCommandController(
    private val attendanceCommandService: AttendanceCommandService
) {
    @Operation(summary = "출석체크 시작", description = "스터디장이 출석체크를 시작합니다. 6자리 출석 코드가 반환됩니다.")
    @PostMapping
    fun startAttendanceCheck(
        @Parameter(description = "스터디 ID", required = true) @PathVariable studyId: Long,
        @Parameter(description = "일정 ID", required = true) @PathVariable scheduleId: Long,
        @RequestHeader("X-Member-Id") memberId: Long
    ): ResponseEntity<ApiResponse<StartAttendanceResponse>> {
        val attendanceCode = attendanceCommandService.startAttendance(studyId, scheduleId, memberId)
        return ResponseEntity.ok(ApiResponse.ok(StartAttendanceResponse(attendanceCode)))
    }

    @Operation(summary = "출석체크 종료", description = "스터디장이 출석체크를 종료합니다.")
    @DeleteMapping
    fun stopAttendanceCheck(
        @Parameter(description = "스터디 ID", required = true) @PathVariable studyId: Long,
        @Parameter(description = "일정 ID", required = true) @PathVariable scheduleId: Long,
        @RequestHeader("X-Member-Id") memberId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        attendanceCommandService.stopAttendance(studyId, scheduleId, memberId)
        return ResponseEntity.ok(ApiResponse.ok())
    }

    @Operation(summary = "출석체크 처리", description = "출석 코드를 입력하여 출석체크를 처리합니다.")
    @PostMapping("/check")
    fun checkAttendance(
        @Parameter(description = "스터디 ID", required = true) @PathVariable studyId: Long,
        @Parameter(description = "일정 ID", required = true) @PathVariable scheduleId: Long,
        @Parameter(description = "6자리 출석 코드", required = true)
        @RequestParam code: String,
        @RequestHeader("X-Member-Id") memberId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        attendanceCommandService.checkAttendance(studyId, scheduleId, code, memberId)
        return ResponseEntity.ok(ApiResponse.ok())
    }
}

data class StartAttendanceResponse(
    val attendanceCode: String
)
