package kr.spot.core.point.application.event

import kr.spot.common.api.exception.GeneralException
import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.event.contract.PointReason
import kr.spot.common.event.contract.StreakMileStone
import kr.spot.common.event.payload.AttendanceCheckedEvent
import kr.spot.common.event.payload.MemberCreatedEvent
import kr.spot.common.id.IdGenerator
import kr.spot.core.point.domain.Point
import kr.spot.core.point.domain.PointHistory
import kr.spot.core.point.infrastrcuture.PointHistoryRepository
import kr.spot.core.point.infrastrcuture.PointRepository
import kr.spot.core.point.infrastrcuture.querydsl.PointCustomRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.time.LocalDateTime

@Component
class PointApplicationEventListener(
    private val idGenerator: IdGenerator,
    private val pointRepository: PointRepository,
    private val pointHistoryRepository: PointHistoryRepository,
    private val pointCustomRepository: PointCustomRepository
) {
    companion object {
        private const val DAILY_MAX_POINTS = 100L
        private const val DAILY_ATTENDANCE_POINTS = 10L
        private const val STREAK_7_DAYS_POINTS = 50L
        private const val STREAK_14_DAYS_POINTS = 100L
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun registerPoint(event: MemberCreatedEvent) {
        pointRepository.save(
            Point.create(
                idGenerator.nextId(),
                event.memberId
            )
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handleAttendanceChecked(event: AttendanceCheckedEvent) {
        val point =
            pointRepository.findWithLockByMemberId(event.memberId)
                ?: throw GeneralException(ErrorStatus.POINT_NOT_FOUND)

        grantPoints(
            point,
            event.eventId,
            event.memberId,
            DAILY_ATTENDANCE_POINTS,
            PointReason.ATTENDANCE
        )

        event.milestone?.let { milestone ->
            val (bonusPoints, reason) =
                when (milestone) {
                    StreakMileStone.ONE_WEEK ->
                        STREAK_7_DAYS_POINTS to PointReason.ATTENDANCE_STREAK_FOR_7_DAYS
                    StreakMileStone.TWO_WEEKS ->
                        STREAK_14_DAYS_POINTS to PointReason.ATTENDANCE_STREAK_FOR_14_DAYS
                }

            grantPoints(
                point = point,
                eventId = "${event.eventId}-${milestone.name.lowercase()}",
                memberId = event.memberId,
                amount = bonusPoints,
                reason = reason
            )
        }
    }

    private fun grantPoints(
        point: Point,
        eventId: String,
        memberId: Long,
        amount: Long,
        reason: PointReason
    ) {
        if (pointHistoryRepository.existsByEventId(eventId)) {
            return
        }

        val gainedPointsToday = pointCustomRepository.getGainedPointsToday(memberId, LocalDateTime.now())

        if (gainedPointsToday >= DAILY_MAX_POINTS) {
            return
        }

        val allowedAmount = minOf(amount, DAILY_MAX_POINTS - gainedPointsToday)
        increasePointAndSave(point, allowedAmount, eventId, memberId, reason)
    }

    private fun increasePointAndSave(
        point: Point,
        amount: Long,
        eventId: String,
        memberId: Long,
        reason: PointReason
    ) {
        point.increaseAmount(amount)
        pointHistoryRepository.save(
            PointHistory.of(
                id = idGenerator.nextId(),
                eventId = eventId,
                memberId = memberId,
                points = amount,
                reason = reason,
                referenceId = null,
                grantedAt = LocalDateTime.now()
            )
        )
    }
}
