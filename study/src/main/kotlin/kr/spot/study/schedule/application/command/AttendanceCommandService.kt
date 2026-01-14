package kr.spot.study.schedule.application.command

import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.exception.GeneralException
import kr.spot.common.id.IdGenerator
import kr.spot.common.ports.GetMemberInfoPort
import kr.spot.study.core.application.validator.StudyAccessValidator
import kr.spot.study.core.domain.enums.StudyMemberStatus
import kr.spot.study.core.infrastructure.jpa.StudyMemberRepository
import kr.spot.study.schedule.domain.Attendance
import kr.spot.study.schedule.domain.Schedule
import kr.spot.study.schedule.domain.enums.AttendanceStatus
import kr.spot.study.schedule.domain.vo.MemberInfo
import kr.spot.study.schedule.infrastructure.jpa.AttendanceRepository
import kr.spot.study.schedule.infrastructure.jpa.ScheduleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AttendanceCommandService(
    private val idGenerator: IdGenerator,
    private val scheduleRepository: ScheduleRepository,
    private val attendanceRepository: AttendanceRepository,
    private val studyMemberRepository: StudyMemberRepository,
    private val studyAccessValidator: StudyAccessValidator,
    private val getMemberInfoPort: GetMemberInfoPort,
) {
    fun startAttendance(
        studyId: Long,
        scheduleId: Long,
        memberId: Long
    ): String {
        studyAccessValidator.validateStudyLeader(studyId, memberId)

        val schedule = getById(scheduleId)
        schedule.startAttendance(studyId)

        val attendanceCode = generateAttendanceCode()
        schedule.updateAttendanceCode(attendanceCode)

        createAttendancesForAllMembers(studyId, scheduleId)

        return attendanceCode
    }

    fun stopAttendance(
        studyId: Long,
        scheduleId: Long,
        memberId: Long
    ) {
        studyAccessValidator.validateStudyLeader(studyId, memberId)

        val schedule = getById(scheduleId)
        schedule.stopAttendance(studyId)

        markAbsentForUndecidedAttendances(scheduleId)
    }

    private fun markAbsentForUndecidedAttendances(scheduleId: Long) {
        val attendances = attendanceRepository.findAllByScheduleId(scheduleId)
        attendances.forEach { it.markAbsentIfUndecided() }
    }

    fun checkAttendance(
        studyId: Long,
        scheduleId: Long,
        code: String,
        memberId: Long
    ) {
        studyAccessValidator.validateStudyMember(studyId, memberId)

        val schedule = getById(scheduleId)
        schedule.validateAttendanceCheckable()
        validateAttendanceCode(schedule, code)

        val attendance =
            attendanceRepository
                .findByScheduleIdAndMemberInfoMemberId(scheduleId, memberId)
                .orElseThrow { GeneralException(ErrorStatus.ATTENDANCE_NOT_FOUND) }

        attendance.markAttendance(AttendanceStatus.PRESENT)
    }

    private fun validateAttendanceCode(
        schedule: Schedule,
        code: String
    ) {
        if (schedule.attendanceCode != code) {
            throw GeneralException(ErrorStatus.INVALID_ATTENDANCE_CODE)
        }
    }

    private fun generateAttendanceCode(): String = (100000..999999).random().toString()

    private fun createAttendancesForAllMembers(
        studyId: Long,
        scheduleId: Long
    ) {
        val activeMembers =
            studyMemberRepository
                .findAllByStudyIdAndStudyMemberStatusIn(studyId, ACTIVE_MEMBER_STATUSES)

        val memberIds = activeMembers.map { it.memberId }
        val memberInfoMap = getMemberInfoPort.getMemberInfo(memberIds)

        val attendances =
            activeMembers.map { studyMember ->
                val info = memberInfoMap[studyMember.memberId]
                val memberInfo =
                    MemberInfo.of(
                        studyMember.memberId,
                        info?.name ?: "",
                        info?.profileImageUrl
                    )
                val attendance = Attendance.createPending(idGenerator.nextId(), scheduleId, memberInfo)
                if (studyMember.studyMemberStatus == StudyMemberStatus.OWNER) {
                    attendance.markAttendance(AttendanceStatus.PRESENT)
                }
                attendance
            }

        attendanceRepository.saveAll(attendances)
    }

    private fun getById(scheduleId: Long): Schedule =
        scheduleRepository.findById(scheduleId).orElseThrow { GeneralException(ErrorStatus.SCHEDULE_NOT_FOUND) }

    companion object {
        private val ACTIVE_MEMBER_STATUSES = listOf(StudyMemberStatus.OWNER, StudyMemberStatus.APPROVED)
    }
}
