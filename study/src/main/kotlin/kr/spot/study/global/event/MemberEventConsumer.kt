package kr.spot.study.global.event

import kr.spot.common.event.Topics
import kr.spot.common.event.consumer.AbstractEventConsumer
import kr.spot.common.event.metrics.EventMetrics
import kr.spot.common.event.payload.MemberProfileUpdatedEvent
import kr.spot.study.review.infrastructure.jpa.ReviewRepository
import kr.spot.study.schedule.infrastructure.jpa.AttendanceRepository
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberEventConsumer(
    private val reviewRepository: ReviewRepository,
    private val attendanceRepository: AttendanceRepository,
    eventMetrics: EventMetrics
) : AbstractEventConsumer<MemberProfileUpdatedEvent>(eventMetrics, CONSUMER_GROUP) {
    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = [Topics.MEMBER_EVENTS],
        groupId = CONSUMER_GROUP,
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    fun handleMemberProfileUpdated(record: ConsumerRecord<String, MemberProfileUpdatedEvent>) {
        processWithMetrics(record) { event ->
            updateDenormalizedColumns(event)
        }
    }

    private fun updateDenormalizedColumns(event: MemberProfileUpdatedEvent) {
        val reviewCount =
            reviewRepository.updateWriterInfo(
                memberId = event.memberId,
                nickname = event.nickname,
                profileImageUrl = event.profileImageUrl
            )

        val attendanceCount =
            attendanceRepository.updateMemberInfo(
                memberId = event.memberId,
                nickname = event.nickname,
                profileImageUrl = event.profileImageUrl
            )

        log.info(
            "[Denormalize] Updated member info - memberId: {}, reviewsUpdated: {}, attendancesUpdated: {}",
            event.memberId,
            reviewCount,
            attendanceCount
        )
    }

    companion object {
        private const val CONSUMER_GROUP = "spot-study-denormalize"
    }
}
