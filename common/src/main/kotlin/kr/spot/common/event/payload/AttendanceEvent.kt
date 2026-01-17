package kr.spot.common.event.payload

import kr.spot.common.event.DomainEvent
import kr.spot.common.event.EventType
import kr.spot.common.event.contract.StreakMileStone
import java.time.LocalDate

data class AttendanceCheckedEvent (
    val memberId: Long,
    val checkedDate: LocalDate,
    val currentStreak: Int,
    val milestone: StreakMileStone?,
) : DomainEvent() {
    override val eventType: EventType = EventType.ATTENDANCE_CHECKED
}
