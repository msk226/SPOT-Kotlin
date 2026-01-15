package kr.spot.common.event.consumer

import kr.spot.common.event.DomainEvent
import kr.spot.common.event.metrics.EventMetrics
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory

abstract class AbstractEventConsumer<T : DomainEvent>(
    private val eventMetrics: EventMetrics,
    private val consumerGroup: String
) {
    private val log = LoggerFactory.getLogger(javaClass)

    protected fun processWithMetrics(
        record: ConsumerRecord<String, T>,
        handler: (T) -> Unit
    ) {
        val event = record.value()
        val topic = record.topic()

        log.debug(
            "[Kafka] Received event - topic: {}, partition: {}, offset: {}, eventType: {}, eventId: {}",
            topic,
            record.partition(),
            record.offset(),
            event.eventType.value,
            event.eventId
        )

        val timer = eventMetrics.getConsumeTimer(event.eventType, consumerGroup)
        val sample = io.micrometer.core.instrument.Timer.start()

        try {
            handler(event)

            log.info(
                "[Kafka] Event processed - topic: {}, eventType: {}, eventId: {}, consumerGroup: {}",
                topic,
                event.eventType.value,
                event.eventId,
                consumerGroup
            )
            eventMetrics.recordConsumed(event.eventType, consumerGroup)
        } catch (ex: Exception) {
            log.error(
                "[Kafka] Failed to process event - topic: {}, eventType: {}, eventId: {}, consumerGroup: {}, error: {}",
                topic,
                event.eventType.value,
                event.eventId,
                consumerGroup,
                ex.message,
                ex
            )
            eventMetrics.recordConsumeFailed(event.eventType, consumerGroup, ex.javaClass.simpleName)
            throw ex
        } finally {
            sample.stop(timer)
        }
    }
}
