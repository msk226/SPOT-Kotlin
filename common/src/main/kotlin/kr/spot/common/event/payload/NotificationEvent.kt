package kr.spot.common.event.payload

import kr.spot.common.event.DomainEvent
import kr.spot.common.event.EventType

data class NotificationEvent(
    val targetMemberId: Long,
    val title: String,
    val content: String,
    val notificationType: NotificationType,
    val referenceId: Long? = null
) : DomainEvent() {
    override val eventType: EventType = EventType.NOTIFICATION_SEND
}

enum class NotificationType {
    STUDY_APPLICATION,
    STUDY_APPROVED,
    STUDY_REJECTED,
    NEW_POST,
    NEW_COMMENT,
    SYSTEM
}
