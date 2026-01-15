package kr.spot.common.event.publisher

import kr.spot.common.event.DomainEvent
import kr.spot.common.event.metrics.EventMetrics
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val eventMetrics: EventMetrics
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun publish(event: DomainEvent) {
        publish(event.eventId, event)
    }

    fun publish(
        key: String,
        event: DomainEvent
    ) {
        val topic = event.topic

        log.debug(
            "[Kafka] Publishing event - topic: {}, eventType: {}, eventId: {}",
            topic,
            event.eventType.value,
            event.eventId
        )

        kafkaTemplate
            .send(topic, key, event)
            .whenComplete { result, ex ->
                if (ex == null) {
                    log.info(
                        "[Kafka] Event published - topic: {}, partition: {}, offset: {}, eventType: {}, eventId: {}",
                        topic,
                        result?.recordMetadata?.partition(),
                        result?.recordMetadata?.offset(),
                        event.eventType.value,
                        event.eventId
                    )
                    eventMetrics.recordPublished(event.eventType)
                } else {
                    log.error(
                        "[Kafka] Failed to publish event - topic: {}, eventType: {}, eventId: {}, error: {}",
                        topic,
                        event.eventType.value,
                        event.eventId,
                        ex.message,
                        ex
                    )
                    eventMetrics.recordPublishFailed(event.eventType, ex.javaClass.simpleName)
                }
            }
    }

    fun publishSync(event: DomainEvent): Boolean = publishSync(event.eventId, event)

    fun publishSync(
        key: String,
        event: DomainEvent
    ): Boolean {
        val topic = event.topic

        return try {
            log.debug(
                "[Kafka] Publishing event (sync) - topic: {}, eventType: {}, eventId: {}",
                topic,
                event.eventType.value,
                event.eventId
            )

            val result = kafkaTemplate.send(topic, key, event).get()

            log.info(
                "[Kafka] Event published (sync) - topic: {}, partition: {}, offset: {}, eventType: {}, eventId: {}",
                topic,
                result.recordMetadata.partition(),
                result.recordMetadata.offset(),
                event.eventType.value,
                event.eventId
            )
            eventMetrics.recordPublished(event.eventType)
            true
        } catch (ex: Exception) {
            log.error(
                "[Kafka] Failed to publish event (sync) - topic: {}, eventType: {}, eventId: {}, error: {}",
                topic,
                event.eventType.value,
                event.eventId,
                ex.message,
                ex
            )
            eventMetrics.recordPublishFailed(event.eventType, ex.javaClass.simpleName)
            false
        }
    }
}
