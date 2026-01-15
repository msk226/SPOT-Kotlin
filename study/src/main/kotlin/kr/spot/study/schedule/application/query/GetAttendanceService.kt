package kr.spot.study.schedule.application.query

import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.api.exception.GeneralException
import kr.spot.study.core.application.validator.StudyAccessValidator
import kr.spot.study.schedule.domain.Schedule
import kr.spot.study.schedule.infrastructure.jpa.AttendanceRepository
import kr.spot.study.schedule.infrastructure.jpa.ScheduleRepository
import kr.spot.study.schedule.presentation.query.dto.GetAttendanceInfoResponse
import kr.spot.study.schedule.presentation.query.dto.GetAttendanceListResponse
import kr.spot.study.schedule.presentation.query.dto.GetAttendanceListResponse.AttendanceInfoResponse
import kr.spot.study.schedule.presentation.query.dto.GetAttendanceListResponse.MemberInfoResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetAttendanceService(
    private val scheduleRepository: ScheduleRepository,
    private val attendanceRepository: AttendanceRepository,
    private val studyAccessValidator: StudyAccessValidator
) {
    fun getAttendanceInfo(
        studyId: Long,
        scheduleId: Long,
        memberId: Long
    ): GetAttendanceInfoResponse {
        studyAccessValidator.validateStudyMember(studyId, memberId)

        val schedule = getById(scheduleId)
        return GetAttendanceInfoResponse.of(
            schedule.attendanceActive,
            schedule.attendanceCode
        )
    }

    fun getAttendanceList(
        studyId: Long,
        scheduleId: Long,
        memberId: Long
    ): GetAttendanceListResponse {
        studyAccessValidator.validateStudyMember(studyId, memberId)

        val attendances = attendanceRepository.findAllByScheduleId(scheduleId)

        val attendanceInfos =
            attendances.map { attendance ->
                AttendanceInfoResponse.from(
                    MemberInfoResponse.from(
                        attendance.memberInfo.memberId,
                        attendance.memberInfo.memberName,
                        attendance.memberInfo.memberProfileImageUrl
                    ),
                    attendance.attendanceStatus.name,
                    attendance.attendedAt
                )
            }

        return GetAttendanceListResponse.from(attendanceInfos, attendanceInfos.size)
    }

    private fun getById(scheduleId: Long): Schedule =
        scheduleRepository.findById(scheduleId).orElseThrow { GeneralException(ErrorStatus.SCHEDULE_NOT_FOUND) }
}
