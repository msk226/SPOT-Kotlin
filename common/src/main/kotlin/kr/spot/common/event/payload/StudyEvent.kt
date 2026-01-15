package kr.spot.common.event.payload

import kr.spot.common.event.DomainEvent
import kr.spot.common.event.EventType

data class StudyCreatedEvent(
    val studyId: Long,
    val leaderId: Long,
    val studyName: String
) : DomainEvent() {
    override val eventType: EventType = EventType.STUDY_CREATED
}

data class StudyMemberJoinedEvent(
    val studyId: Long,
    val memberId: Long,
    val studyName: String
) : DomainEvent() {
    override val eventType: EventType = EventType.STUDY_MEMBER_JOINED
}

data class StudyApplicationReceivedEvent(
    val studyId: Long,
    val applicantId: Long,
    val leaderId: Long,
    val studyName: String
) : DomainEvent() {
    override val eventType: EventType = EventType.STUDY_APPLICATION_RECEIVED
}
