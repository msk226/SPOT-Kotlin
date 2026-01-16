package kr.spot.study.todo.presentation.query.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "투두 목록 응답")
data class GetTodoListResponse(
    @Schema(description = "미완료 투두 목록")
    val pending: List<TodoResponse>,
    @Schema(description = "완료된 투두 목록")
    val completed: List<TodoResponse>
) {
    companion object {
        fun from(
            pending: List<TodoResponse>,
            completed: List<TodoResponse>
        ): GetTodoListResponse = GetTodoListResponse(pending, completed)
    }

    @Schema(description = "투두 상세 정보")
    data class TodoResponse(
        @Schema(description = "투두 ID", example = "1234567890")
        val id: Long,
        @Schema(description = "투두 내용", example = "과제 제출하기")
        val content: String,
        @Schema(description = "마감일", example = "2025-01-15")
        val dueDate: LocalDate,
        @Schema(description = "완료 여부", example = "false")
        val isCompleted: Boolean
    ) {
        companion object {
            fun from(
                id: Long,
                content: String,
                dueDate: LocalDate,
                isCompleted: Boolean
            ): TodoResponse = TodoResponse(id, content, dueDate, isCompleted)
        }
    }
}
