package kr.spot.study.schedule.presentation.query

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.study.schedule.application.query.GetScheduleService
import kr.spot.study.schedule.presentation.query.dto.GetScheduleListResponse
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@Tag(name = "스터디 일정")
@RestController
@RequestMapping("/api/studies/{studyId}/schedules")
class ScheduleQueryController(
    private val getScheduleService: GetScheduleService
) {
    @Operation(summary = "월별 일정 조회", description = "해당 연/월의 모든 일정을 조회합니다.")
    @GetMapping("/monthly")
    fun getMonthlySchedules(
        @Parameter(description = "스터디 ID", required = true) @PathVariable studyId: Long,
        @Parameter(description = "연도", example = "2025") @RequestParam year: Int,
        @Parameter(description = "월", example = "1") @RequestParam month: Int
    ): ResponseEntity<ApiResponse<GetScheduleListResponse>> {
        val response = getScheduleService.getMonthlySchedules(studyId, year, month)
        return ResponseEntity.ok(ApiResponse.ok(response))
    }

    @Operation(summary = "주간 일정 조회", description = "해당 날짜가 속한 주(월~일)의 모든 일정을 조회합니다.")
    @GetMapping("/weekly")
    fun getWeeklySchedules(
        @Parameter(description = "스터디 ID", required = true) @PathVariable studyId: Long,
        @Parameter(description = "조회 기준 날짜", example = "2025-01-15")
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): ResponseEntity<ApiResponse<GetScheduleListResponse>> {
        val response = getScheduleService.getWeeklySchedules(studyId, date)
        return ResponseEntity.ok(ApiResponse.ok(response))
    }

    @Operation(summary = "다가오는 일정 조회", description = "현재 시점 이후의 가장 가까운 일정 2개를 조회합니다.")
    @GetMapping("/upcoming")
    fun getUpcomingSchedules(
        @Parameter(description = "스터디 ID", required = true) @PathVariable studyId: Long
    ): ResponseEntity<ApiResponse<GetScheduleListResponse>> {
        val response = getScheduleService.getUpcomingSchedules(studyId)
        return ResponseEntity.ok(ApiResponse.ok(response))
    }
}
