package kr.spot.study.todo.presentation.command

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.study.todo.application.command.ManageTodoService
import kr.spot.study.todo.presentation.command.dto.CreateTodoRequest
import kr.spot.study.todo.presentation.command.dto.CreateTodoResponse
import kr.spot.study.todo.presentation.command.dto.UpdateTodoRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "스터디 투두")
@RestController
@RequestMapping("/api/studies/{studyId}/todos")
class TodoCommandController(
    private val manageTodoService: ManageTodoService
) {
    @Operation(summary = "투두 생성", description = "스터디에 새로운 투두를 생성합니다.")
    @PostMapping
    fun createTodo(
        @Parameter(description = "스터디 ID", required = true) @PathVariable studyId: Long,
        @RequestHeader memberId: Long,
        @RequestBody request: CreateTodoRequest
    ): ResponseEntity<ApiResponse<CreateTodoResponse>> {
        val todoId = manageTodoService.createTodo(studyId, memberId, request)
        return ResponseEntity.ok(
            ApiResponse.created(CreateTodoResponse.from(todoId))
        )
    }

    @Operation(summary = "투두 수정", description = "투두 내용과 마감일을 수정합니다.")
    @PatchMapping("/{todoId}")
    fun updateTodo(
        @Parameter(description = "스터디 ID", required = true) @PathVariable studyId: Long,
        @Parameter(description = "투두 ID", required = true) @PathVariable todoId: Long,
        @RequestHeader memberId: Long,
        @RequestBody request: UpdateTodoRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        manageTodoService.updateTodo(studyId, todoId, memberId, request)
        return ResponseEntity.ok(ApiResponse.ok())
    }

    @Operation(summary = "투두 완료", description = "투두를 완료 상태로 변경합니다.")
    @PostMapping("/{todoId}/complete")
    fun completeTodo(
        @Parameter(description = "스터디 ID", required = true) @PathVariable studyId: Long,
        @Parameter(description = "투두 ID", required = true) @PathVariable todoId: Long,
        @RequestHeader memberId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        manageTodoService.completeTodo(studyId, todoId, memberId)
        return ResponseEntity.ok(ApiResponse.ok())
    }

    @Operation(summary = "투두 미완료로 변경", description = "투두를 미완료 상태로 변경합니다.")
    @PostMapping("/{todoId}/uncomplete")
    fun uncompleteTodo(
        @Parameter(description = "스터디 ID", required = true) @PathVariable studyId: Long,
        @Parameter(description = "투두 ID", required = true) @PathVariable todoId: Long,
        @RequestHeader memberId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        manageTodoService.uncompleteTodo(studyId, todoId, memberId)
        return ResponseEntity.ok(ApiResponse.ok())
    }

    @Operation(summary = "투두 삭제", description = "투두를 삭제합니다.")
    @DeleteMapping("/{todoId}")
    fun deleteTodo(
        @Parameter(description = "스터디 ID", required = true) @PathVariable studyId: Long,
        @Parameter(description = "투두 ID", required = true) @PathVariable todoId: Long,
        @RequestHeader memberId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        manageTodoService.deleteTodo(studyId, todoId, memberId)
        return ResponseEntity.ok(ApiResponse.ok())
    }
}
