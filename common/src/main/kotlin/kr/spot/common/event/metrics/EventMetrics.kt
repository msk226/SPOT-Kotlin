package kr.spot.common.event.metrics

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import kr.spot.common.event.EventType
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class EventMetrics(private val meterRegistry: MeterRegistry) {

    private val publishedCounters = ConcurrentHashMap<String, Counter>()
    private val publishFailedCounters = ConcurrentHashMap<String, Counter>()
    private val consumedCounters = ConcurrentHashMap<String, Counter>()
    private val consumeFailedCounters = ConcurrentHashMap<String, Counter>()
    private val consumeTimers = ConcurrentHashMap<String, Timer>()

    fun recordPublished(eventType: EventType) {
        val key = "${eventType.value}:${eventType.topic}"
        publishedCounters.computeIfAbsent(key) {
            Counter.builder(EVENTS_PUBLISHED)
                .tag(TAG_EVENT_TYPE, eventType.value)
                .tag(TAG_TOPIC, eventType.topic)
                .description("Number of events published successfully")
                .register(meterRegistry)
        }.increment()
    }

    fun recordPublishFailed(eventType: EventType, errorType: String) {
        val key = "${eventType.value}:${eventType.topic}:$errorType"
        publishFailedCounters.computeIfAbsent(key) {
            Counter.builder(EVENTS_PUBLISH_FAILED)
                .tag(TAG_EVENT_TYPE, eventType.value)
                .tag(TAG_TOPIC, eventType.topic)
                .tag(TAG_ERROR, errorType)
                .description("Number of events failed to publish")
                .register(meterRegistry)
        }.increment()
    }

    fun recordConsumed(eventType: EventType, consumerGroup: String) {
        val key = "${eventType.value}:${eventType.topic}:$consumerGroup"
        consumedCounters.computeIfAbsent(key) {
            Counter.builder(EVENTS_CONSUMED)
                .tag(TAG_EVENT_TYPE, eventType.value)
                .tag(TAG_TOPIC, eventType.topic)
                .tag(TAG_CONSUMER_GROUP, consumerGroup)
                .description("Number of events consumed successfully")
                .register(meterRegistry)
        }.increment()
    }

    fun recordConsumeFailed(eventType: EventType, consumerGroup: String, errorType: String) {
        val key = "${eventType.value}:${eventType.topic}:$consumerGroup:$errorType"
        consumeFailedCounters.computeIfAbsent(key) {
            Counter.builder(EVENTS_CONSUME_FAILED)
                .tag(TAG_EVENT_TYPE, eventType.value)
                .tag(TAG_TOPIC, eventType.topic)
                .tag(TAG_CONSUMER_GROUP, consumerGroup)
                .tag(TAG_ERROR, errorType)
                .description("Number of events failed to consume")
                .register(meterRegistry)
        }.increment()
    }

    fun getConsumeTimer(eventType: EventType, consumerGroup: String): Timer {
        val key = "${eventType.value}:$consumerGroup"
        return consumeTimers.computeIfAbsent(key) {
            Timer.builder(EVENTS_CONSUME_DURATION)
                .tag(TAG_EVENT_TYPE, eventType.value)
                .tag(TAG_CONSUMER_GROUP, consumerGroup)
                .description("Time taken to process an event")
                .register(meterRegistry)
        }
    }

    companion object {
        private const val EVENTS_PUBLISHED = "spot.events.published"
        private const val EVENTS_PUBLISH_FAILED = "spot.events.publish.failed"
        private const val EVENTS_CONSUMED = "spot.events.consumed"
        private const val EVENTS_CONSUME_FAILED = "spot.events.consume.failed"
        private const val EVENTS_CONSUME_DURATION = "spot.events.consume.duration"

        private const val TAG_EVENT_TYPE = "eventType"
        private const val TAG_TOPIC = "topic"
        private const val TAG_CONSUMER_GROUP = "consumerGroup"
        private const val TAG_ERROR = "error"
    }
}
