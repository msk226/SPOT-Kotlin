package kr.spot.worker.admin.sse

import kr.spot.worker.admin.stream.DashboardStatsService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class DashboardSseEventListener(
    private val dashboardSseService: DashboardSseService,
    private val dashboardStatsService: DashboardStatsService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Async
    @EventListener
    fun onStatsUpdated(event: DashboardStatsUpdatedEvent) {
        if (dashboardSseService.getSubscriberCount() == 0) {
            return
        }

        log.debug("Stats updated event received: {}", event.eventType)

        val stats = dashboardStatsService.getRealtimeStats()
        dashboardSseService.broadcast(stats)
    }
}
