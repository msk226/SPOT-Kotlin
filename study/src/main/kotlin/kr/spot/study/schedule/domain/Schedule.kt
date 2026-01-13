package kr.spot.study.schedule.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.exception.GeneralException
import kr.spot.study.global.domain.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@SQLDelete(sql = "UPDATE schedule SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class Schedule private constructor(
    @Id
    val id: Long,
    val studyId: Long,
    @Column(nullable = false)
    val title: String,
    val locationMemo: String?,
    val startAt: LocalDateTime?,
    val endAt: LocalDateTime?,
    attendanceActive: Boolean,
    attendanceQrCodeImageUrl: String?
) : BaseEntity() {
    var attendanceActive: Boolean = attendanceActive
        private set

    var attendanceQrCodeImageUrl: String? = attendanceQrCodeImageUrl
        private set

    fun delete(studyId: Long) {
        validateIsValidAccess(studyId)
        super.delete()
    }

    fun isOngoing(now: LocalDateTime): Boolean {
        if (startAt == null || endAt == null) {
            return false
        }
        return !now.isBefore(startAt) && !now.isAfter(endAt)
    }

    fun startAttendance(studyId: Long) {
        validateIsValidAccess(studyId)
        validateIsOngoing()
        validateIsNotAttendanceActive()
        this.attendanceActive = true
    }

    fun stopAttendance(studyId: Long) {
        validateIsValidAccess(studyId)
        this.attendanceActive = false
        this.attendanceQrCodeImageUrl = null
    }

    fun updateQrCodeImageUrl(url: String) {
        this.attendanceQrCodeImageUrl = url
    }

    fun validateAttendanceCheckable() {
        validateIsAttendanceActive()
        validateIsOngoing()
    }

    private fun validateIsValidAccess(studyId: Long) {
        if (studyId != this.studyId) {
            throw GeneralException(ErrorStatus.SCHEDULE_ACCESS_DENIED)
        }
    }

    private fun validateIsOngoing() {
        if (!isOngoing(LocalDateTime.now())) {
            throw GeneralException(ErrorStatus.ATTENDANCE_NOT_IN_SCHEDULE_TIME)
        }
    }

    private fun validateIsAttendanceActive() {
        if (!this.attendanceActive) {
            throw GeneralException(ErrorStatus.ATTENDANCE_NOT_STARTED)
        }
    }

    private fun validateIsNotAttendanceActive() {
        if (this.attendanceActive) {
            throw GeneralException(ErrorStatus.SCHEDULE_QR_CODE_ALREADY_ASSIGNED)
        }
    }

    companion object {
        fun of(
            id: Long,
            studyId: Long,
            title: String,
            locationMemo: String?,
            startAt: LocalDateTime?,
            endAt: LocalDateTime?
        ): Schedule =
            Schedule(
                id = id,
                studyId = studyId,
                title = title,
                locationMemo = locationMemo,
                startAt = startAt,
                endAt = endAt,
                attendanceActive = false,
                attendanceQrCodeImageUrl = null
            )
    }
}
