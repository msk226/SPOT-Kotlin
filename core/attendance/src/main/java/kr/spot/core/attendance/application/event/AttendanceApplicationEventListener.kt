package kr.spot.core.attendance.application.event

import kr.spot.common.event.payload.MemberCreatedEvent
import kr.spot.common.id.IdGenerator
import kr.spot.core.attendance.domain.AttendanceStreak
import kr.spot.core.attendance.infrastructure.AttendanceStreakRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class AttendanceApplicationEventListener(
    private val idGenerator: IdGenerator,
    private val attendanceStreakRepository: AttendanceStreakRepository
) {
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun registerAttendanceStreak(event: MemberCreatedEvent) {
        val attendanceStreak =
            AttendanceStreak.create(
                id = idGenerator.nextId(),
                memberId = event.memberId
            )
        attendanceStreakRepository.save(attendanceStreak)
    }
}
