package kr.spot.study.todo.presentation.query

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.study.todo.application.query.GetTodoService
import kr.spot.study.todo.presentation.query.dto.GetTodoListResponse
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@Tag(name = "스터디 투두")
@RestController
@RequestMapping("/api/studies/{studyId}/todos")
class TodoQueryController(
    private val getTodoService: GetTodoService
) {
    @Operation(
        summary = "날짜별 투두 조회",
        description = "특정 날짜의 투두를 조회합니다. 미완료 항목이 먼저, 완료 항목은 별도로 조회됩니다."
    )
    @GetMapping("/members/{memberId}")
    fun getTodosByDate(
        @PathVariable studyId: Long,
        @PathVariable memberId: Long,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): ResponseEntity<ApiResponse<GetTodoListResponse>> {
        val response = getTodoService.getTodosByDate(studyId, memberId, date)
        return ResponseEntity.ok(ApiResponse.ok(response))
    }
}
