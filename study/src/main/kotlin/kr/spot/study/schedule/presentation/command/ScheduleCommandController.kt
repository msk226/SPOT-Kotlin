package kr.spot.study.schedule.presentation.command

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.study.schedule.application.command.ManageScheduleService
import kr.spot.study.schedule.presentation.command.dto.CreateScheduleRequest
import kr.spot.study.schedule.presentation.command.dto.CreateScheduleResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "스터디 일정")
@RestController
@RequestMapping("/api/studies/{studyId}/schedules")
class ScheduleCommandController(
    private val manageScheduleService: ManageScheduleService
) {
    @Operation(summary = "스터디 일정 생성", description = "스터디에 새로운 일정을 생성합니다.")
    @PostMapping
    fun createSchedule(
        @Parameter(description = "스터디 ID", required = true) @PathVariable studyId: Long,
        @RequestBody request: CreateScheduleRequest,
        @Parameter(hidden = true) @RequestHeader memberId: Long
    ): ResponseEntity<ApiResponse<CreateScheduleResponse>> {
        val scheduleId = manageScheduleService.createSchedule(request, studyId, memberId)
        return ResponseEntity.ok(
            ApiResponse.created(CreateScheduleResponse.from(scheduleId))
        )
    }

    @Operation(summary = "스터디 일정 삭제", description = "스터디의 일정을 삭제합니다.")
    @DeleteMapping("/{scheduleId}")
    fun deleteSchedule(
        @Parameter(description = "스터디 ID", required = true) @PathVariable studyId: Long,
        @Parameter(description = "일정 ID", required = true) @PathVariable scheduleId: Long,
        @Parameter(hidden = true) @RequestHeader memberId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        manageScheduleService.deleteSchedule(studyId, scheduleId, memberId)
        return ResponseEntity.ok(ApiResponse.ok())
    }
}
