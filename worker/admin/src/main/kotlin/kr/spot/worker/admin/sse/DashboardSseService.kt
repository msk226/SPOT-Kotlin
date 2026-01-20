package kr.spot.worker.admin.sse

import kr.spot.worker.admin.domain.RealtimeStats
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.CopyOnWriteArrayList

@Service
class DashboardSseService {
    private val log = LoggerFactory.getLogger(javaClass)
    private val emitters = CopyOnWriteArrayList<SseEmitter>()

    fun subscribe(initialStats: RealtimeStats): SseEmitter {
        val emitter = SseEmitter(0L) // 타임아웃 없음
        emitters.add(emitter)

        emitter.onCompletion {
            log.debug("SSE connection completed")
            emitters.remove(emitter)
        }
        emitter.onTimeout {
            log.debug("SSE connection timed out")
            emitters.remove(emitter)
        }
        emitter.onError {
            log.debug("SSE connection error: {}", it.message)
            emitters.remove(emitter)
        }

        // 연결 즉시 현재 상태 전송
        sendToEmitter(emitter, "connected", mapOf("message" to "SSE connection established"))
        sendToEmitter(emitter, "stats", initialStats)

        log.info("New SSE subscriber connected. Total subscribers: {}", emitters.size)
        return emitter
    }

    fun broadcast(stats: RealtimeStats) {
        if (emitters.isEmpty()) {
            return
        }

        log.debug("Broadcasting stats to {} subscribers", emitters.size)
        emitters.forEach { emitter ->
            sendToEmitter(emitter, "stats", stats)
        }
    }

    private fun sendToEmitter(
        emitter: SseEmitter,
        eventName: String,
        data: Any
    ) {
        try {
            emitter.send(
                SseEmitter
                    .event()
                    .name(eventName)
                    .data(data)
            )
        } catch (e: Exception) {
            log.debug("Failed to send SSE event: {}", e.message)
            emitters.remove(emitter)
        }
    }

    fun getSubscriberCount(): Int = emitters.size
}
