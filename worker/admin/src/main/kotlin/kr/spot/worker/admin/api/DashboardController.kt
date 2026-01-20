package kr.spot.worker.admin.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.worker.admin.domain.HourlyStats
import kr.spot.worker.admin.domain.RealtimeStats
import kr.spot.worker.admin.stream.DashboardStatsService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@Tag(name = "Admin Dashboard", description = "관리자 대시보드 실시간 통계 API")
@RestController
@RequestMapping("/api/admin/dashboard")
class DashboardController(
    private val dashboardStatsService: DashboardStatsService
) {
    @Operation(summary = "실시간 통계 조회", description = "오늘 또는 특정 날짜의 실시간 집계 통계를 조회합니다")
    @GetMapping("/stats/realtime")
    fun getRealtimeStats(
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        date: LocalDate?
    ): ApiResponse<RealtimeStats> {
        val stats = dashboardStatsService.getRealtimeStats(date ?: LocalDate.now())
        return ApiResponse.ok(stats)
    }

    @Operation(summary = "시간대별 통계 조회", description = "특정 날짜의 시간대별 통계를 조회합니다 (차트용)")
    @GetMapping("/stats/hourly")
    fun getHourlyStats(
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        date: LocalDate?
    ): ApiResponse<List<HourlyStats>> {
        val stats = dashboardStatsService.getHourlyStats(date ?: LocalDate.now())
        return ApiResponse.ok(stats)
    }
}
