package kr.spot.core.point.application.event

import kr.spot.common.event.payload.MemberCreatedEvent
import kr.spot.common.id.IdGenerator
import kr.spot.core.point.domain.Point
import kr.spot.core.point.infrastrcuture.PointRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PointApplicationEventListener (
    private val idGenerator: IdGenerator,
    private val pointRepository: PointRepository
){

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun registerPoint(event: MemberCreatedEvent) {
        pointRepository.save(Point.create(
            idGenerator.nextId(),
            event.memberId
        ))
    }
}
