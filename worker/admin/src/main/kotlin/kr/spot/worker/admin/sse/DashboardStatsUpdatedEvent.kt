package kr.spot.worker.admin.sse

/**
 * 대시보드 통계가 업데이트되었음을 알리는 이벤트
 */
data class DashboardStatsUpdatedEvent(
    val eventType: StatsEventType
)

enum class StatsEventType {
    MEMBER_CREATED,
    ATTENDANCE_CHECKED,
    POINT_GRANTED
}
