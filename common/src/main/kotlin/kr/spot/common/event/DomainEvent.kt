package kr.spot.common.event

import java.time.LocalDateTime
import java.util.UUID

abstract class DomainEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val occurredAt: LocalDateTime = LocalDateTime.now()
) {
    abstract val eventType: EventType

    val topic: String
        get() = eventType.topic
}
