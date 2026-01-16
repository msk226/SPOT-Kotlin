package kr.spot.common.event.payload

import kr.spot.common.event.DomainEvent
import kr.spot.common.event.EventType

data class MemberCreatedEvent(
    val memberId: Long,
) : DomainEvent() {
    override val eventType: EventType = EventType.MEMBER_CREATED
}

data class MemberProfileUpdatedEvent(
    val memberId: Long,
    val nickname: String,
    val profileImageUrl: String?
) : DomainEvent() {
    override val eventType: EventType = EventType.MEMBER_PROFILE_UPDATED
}

data class MemberDeletedEvent(
    val memberId: Long
) : DomainEvent() {
    override val eventType: EventType = EventType.MEMBER_DELETED
}
