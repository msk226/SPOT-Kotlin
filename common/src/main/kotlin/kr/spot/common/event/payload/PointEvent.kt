package kr.spot.common.event.payload

import kr.spot.common.event.DomainEvent
import kr.spot.common.event.EventType
import kr.spot.common.event.contract.PointReason
import java.time.LocalDateTime

data class PointGrantedEvent(
    val memberId: Long,
    val points: Long,
    val reason: PointReason,
    val issuer: PointIssuer,
    val referenceId: Long?,
    val grantedAt: LocalDateTime = LocalDateTime.now()
) : DomainEvent() {
    override val eventType: EventType = EventType.POINT_GRANTED
}

enum class PointIssuer {
    CORE,
    STUDY,
    WORKER,
}
