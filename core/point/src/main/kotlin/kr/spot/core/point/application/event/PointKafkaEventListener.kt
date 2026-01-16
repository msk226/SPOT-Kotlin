package kr.spot.core.point.application.event

import jakarta.transaction.Transactional
import kr.spot.common.event.consumer.AbstractEventConsumer
import kr.spot.common.event.metrics.EventMetrics
import kr.spot.common.event.payload.PointGrantedEvent
import kr.spot.common.id.IdGenerator
import kr.spot.core.point.domain.PointHistory
import kr.spot.core.point.infrastrcuture.PointHistoryRepository
import kr.spot.core.point.infrastrcuture.PointRepository
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PointKafkaEventListener(
    eventMetrics: EventMetrics,
    private val idGenerator: IdGenerator,
    private val pointHistoryRepository: PointHistoryRepository,
    private val pointRepository: PointRepository
) : AbstractEventConsumer<PointGrantedEvent>(
    eventMetrics = eventMetrics,
    consumerGroup = "core-point"
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    @KafkaListener(topics = ["point-granted"], groupId = "core-point")
    fun onPointGranted(record: ConsumerRecord<String, PointGrantedEvent>) {
        processWithMetrics(record, ::handlePointGranted)
    }

    private fun handlePointGranted(event: PointGrantedEvent) {
        if (isDuplicate(event.eventId)) {
            return
        }

        val point = findPointByMemberIdWithLock(event.memberId) ?: run {
            log.warn("[Kafka] Member not found - memberId: {}, eventId: {}", event.memberId, event.eventId)
            return
        }

        pointHistoryRepository.save(createHistory(event))
        point.increaseAmount(event.points)
    }

    private fun isDuplicate(eventId: String): Boolean {
        val duplicated = pointHistoryRepository.existsByEventId(eventId)
        if (duplicated) {
            log.info("[Kafka] Duplicate event skipped - eventId: {}", eventId)
        }
        return duplicated
    }

    private fun createHistory(event: PointGrantedEvent): PointHistory =
        PointHistory.of(
            id = idGenerator.nextId(),
            eventId = event.eventId,
            memberId = event.memberId,
            points = event.points,
            reason = event.reason,
            referenceId = event.referenceId ?: 0L,
            grantedAt = event.grantedAt
        )

    private fun findPointByMemberIdWithLock(memberId: Long) =
        pointRepository.findWithLockByMemberId(memberId)
}
