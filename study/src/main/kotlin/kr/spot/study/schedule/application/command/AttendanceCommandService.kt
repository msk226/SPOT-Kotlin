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
    ) {
        studyAccessValidator.validateStudyLeader(studyId, memberId)

        val schedule = getById(scheduleId)
        schedule.startAttendance(studyId)

        createAttendancesForAllMembers(studyId, scheduleId)
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

//    fun checkAttendance(
//        encryptedToken: String,
//        memberId: Long
//    ) {
//        val tokenData =
//
//        studyAccessValidator.validateStudyMember(tokenData.studyId, memberId)
//
//        val schedule = scheduleRepository.getByIdOrThrow(tokenData.scheduleId)
//        schedule.validateAttendanceCheckable()
//
//        val attendance =
//            attendanceRepository
//                .findByScheduleIdAndMemberInfoMemberId(tokenData.scheduleId, memberId)
//                .orElseThrow { GeneralException(ErrorStatus.STUDY_MEMBER_NOT_FOUND) }
//
//        attendance.markAttendance(AttendanceStatus.PRESENT)
//    }

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
