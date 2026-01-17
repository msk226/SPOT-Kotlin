package kr.spot.core.attendance.application.command

import kr.spot.common.api.exception.GeneralException
import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.event.payload.AttendanceCheckedEvent
import kr.spot.common.id.IdGenerator
import kr.spot.core.attendance.domain.AttendanceCheck
import kr.spot.core.attendance.infrastructure.AttendanceCheckRepository
import kr.spot.core.attendance.infrastructure.AttendanceStreakRepository
import kr.spot.core.attendance.presentation.command.dto.AttendanceCheckResult
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class AttendanceCheckCommandService (
    private val idGenerator: IdGenerator,
    private val attendanceCheckRepository: AttendanceCheckRepository,
    private val attendanceStreakRepository: AttendanceStreakRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
){

    fun checkIn(memberId: Long) : AttendanceCheckResult {
        val today = LocalDate.now()

        if (attendanceCheckRepository.existsByMemberIdAndCheckDate(memberId, today)) {
            throw GeneralException(ErrorStatus.ATTENDANCE_ALREADY_CHECKED)
        }

        val attendanceCheck = AttendanceCheck.of(idGenerator.nextId(), memberId)
        attendanceCheckRepository.save(attendanceCheck)

        val attendanceStreak =
            attendanceStreakRepository.findByMemberId(memberId)
                ?: throw GeneralException(ErrorStatus.ATTENDANCE_STREAK_NOT_FOUND)

        val mileStone = attendanceStreak.recordAttendance(today)

        applicationEventPublisher.publishEvent(
            AttendanceCheckedEvent(
                memberId = memberId,
                checkedDate = today,
                currentStreak = attendanceStreak.currentStreak,
                milestone = mileStone
            )
        )

        return AttendanceCheckResult(
            attendanceStreak.currentStreak,
            attendanceStreak.maxStreak,
            mileStone
        )
    }
}
