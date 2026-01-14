package kr.spot.study.schedule.presentation.query

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.study.schedule.application.query.GetAttendanceService
import kr.spot.study.schedule.presentation.query.dto.GetAttendanceInfoResponse
import kr.spot.study.schedule.presentation.query.dto.GetAttendanceListResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "스터디 일정 - 출석체크", description = "스터디 일정 출석체크 관련 API")
@RestController
@RequestMapping("/api/studies/{studyId}/schedules/{scheduleId}/attendance")
class AttendanceQueryController(
    private val getAttendanceService: GetAttendanceService
) {
    @Operation(summary = "출석 목록 조회", description = "해당 일정의 출석 목록을 조회합니다.")
    @GetMapping
    fun getAttendanceList(
        @Parameter(description = "스터디 ID", required = true) @PathVariable studyId: Long,
        @Parameter(description = "일정 ID", required = true) @PathVariable scheduleId: Long,
        @RequestHeader("X-Member-Id") memberId: Long
    ): ResponseEntity<ApiResponse<GetAttendanceListResponse>> {
        val response = getAttendanceService.getAttendanceList(studyId, scheduleId, memberId)
        return ResponseEntity.ok(ApiResponse.ok(response))
    }

    @Operation(summary = "출석 정보 조회", description = "출석체크 활성 상태 및 출석 코드를 조회합니다.")
    @GetMapping("/info")
    fun getAttendanceInfo(
        @Parameter(description = "스터디 ID", required = true) @PathVariable studyId: Long,
        @Parameter(description = "일정 ID", required = true) @PathVariable scheduleId: Long,
        @RequestHeader("X-Member-Id") memberId: Long
    ): ResponseEntity<ApiResponse<GetAttendanceInfoResponse>> {
        val response = getAttendanceService.getAttendanceInfo(studyId, scheduleId, memberId)
        return ResponseEntity.ok(ApiResponse.ok(response))
    }
}
