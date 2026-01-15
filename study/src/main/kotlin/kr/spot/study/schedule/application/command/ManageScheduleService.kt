package kr.spot.study.schedule.application.command

import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.api.exception.GeneralException
import kr.spot.common.id.IdGenerator
import kr.spot.study.core.application.validator.StudyAccessValidator
import kr.spot.study.schedule.domain.Schedule
import kr.spot.study.schedule.infrastructure.jpa.ScheduleRepository
import kr.spot.study.schedule.presentation.command.dto.CreateScheduleRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ManageScheduleService(
    private val idGenerator: IdGenerator,
    private val scheduleRepository: ScheduleRepository,
    private val studyAccessValidator: StudyAccessValidator
) {
    fun createSchedule(
        request: CreateScheduleRequest,
        studyId: Long,
        memberId: Long
    ): Long {
        studyAccessValidator.validateStudyMember(studyId, memberId)
        val scheduleId = idGenerator.nextId()
        val schedule =
            Schedule.of(
                scheduleId,
                studyId,
                request.title,
                request.locationInfo,
                request.startAt,
                request.endAt
            )

        scheduleRepository.save(schedule)
        return scheduleId
    }

    fun deleteSchedule(
        studyId: Long,
        scheduleId: Long,
        memberId: Long
    ) {
        studyAccessValidator.validateStudyMember(studyId, memberId)
        val schedule = getById(scheduleId)
        schedule.delete(studyId)
    }

    private fun getById(scheduleId: Long): Schedule =
        scheduleRepository.findById(scheduleId).orElseThrow { GeneralException(ErrorStatus.SCHEDULE_NOT_FOUND) }
}
