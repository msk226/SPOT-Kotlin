package kr.spot.core.point.application.event

import jakarta.transaction.Transactional
import kr.spot.common.event.Topics
import kr.spot.common.event.consumer.AbstractEventConsumer
import kr.spot.common.event.metrics.EventMetrics
import kr.spot.common.event.payload.PointGrantedEvent
import kr.spot.common.id.IdGenerator
import kr.spot.core.point.domain.PointHistory
import kr.spot.core.point.infrastrcuture.PointHistoryRepository
import kr.spot.core.point.infrastrcuture.PointRepository
import kr.spot.core.point.infrastrcuture.querydsl.PointCustomRepository
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class PointKafkaEventListener(
    eventMetrics: EventMetrics,
    private val idGenerator: IdGenerator,
    private val pointHistoryRepository: PointHistoryRepository,
    private val pointRepository: PointRepository,
    private val pointCustomRepository: PointCustomRepository
) : AbstractEventConsumer<PointGrantedEvent>(
        eventMetrics = eventMetrics,
        consumerGroup = "core-point"
    ) {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val DAILY_MAX_POINTS = 100L
    }

    @Transactional
    @KafkaListener(topics = [Topics.POINT_EVENTS], groupId = "core-point")
    fun onPointGranted(record: ConsumerRecord<String, PointGrantedEvent>) {
        processWithMetrics(record, ::handlePointGranted)
    }

    private fun handlePointGranted(event: PointGrantedEvent) {
        if (isDuplicate(event.eventId)) {
            return
        }

        val point =
            findPointByMemberIdWithLock(event.memberId) ?: run {
                log.warn("[Kafka] Member not found - memberId: {}, eventId: {}", event.memberId, event.eventId)
                return
            }

        val gainedPointsToday = pointCustomRepository.getGainedPointsToday(event.memberId, LocalDateTime.now())

        if (gainedPointsToday >= DAILY_MAX_POINTS) {
            log.info("[Kafka] Daily limit reached - memberId: {}, gained: {}", event.memberId, gainedPointsToday)
            return
        }

        val allowedAmount = minOf(event.points, DAILY_MAX_POINTS - gainedPointsToday)
        pointHistoryRepository.save(createHistory(event, allowedAmount))
        point.increaseAmount(allowedAmount)
    }

    private fun isDuplicate(eventId: String): Boolean {
        val duplicated = pointHistoryRepository.existsByEventId(eventId)
        if (duplicated) {
            log.info("[Kafka] Duplicate event skipped - eventId: {}", eventId)
        }
        return duplicated
    }

    private fun createHistory(event: PointGrantedEvent, points: Long): PointHistory =
        PointHistory.of(
            id = idGenerator.nextId(),
            eventId = event.eventId,
            memberId = event.memberId,
            points = points,
            reason = event.reason,
            referenceId = event.referenceId ?: 0L,
            grantedAt = event.grantedAt
        )

    private fun findPointByMemberIdWithLock(memberId: Long) = pointRepository.findWithLockByMemberId(memberId)
}
