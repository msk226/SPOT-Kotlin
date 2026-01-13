package kr.spot.study.todo.application.query

import kr.spot.study.todo.domain.Todo
import kr.spot.study.todo.infrastructure.jpa.querydsl.TodoQueryRepository
import kr.spot.study.todo.presentation.query.dto.GetTodoListResponse
import kr.spot.study.todo.presentation.query.dto.GetTodoListResponse.TodoResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class GetTodoService(
    private val todoQueryRepository: TodoQueryRepository
) {
    fun getTodosByDate(
        studyId: Long,
        memberId: Long,
        dueDate: LocalDate
    ): GetTodoListResponse {
        val todos = todoQueryRepository.findByStudyIdAndMemberIdAndDueDate(studyId, memberId, dueDate)
        return toResponse(todos)
    }

    private fun toResponse(todos: List<Todo>): GetTodoListResponse {
        val pending =
            todos
                .filter { it.isCompleted != true }
                .map { toTodoResponse(it) }

        val completed =
            todos
                .filter { it.isCompleted == true }
                .map { toTodoResponse(it) }

        return GetTodoListResponse.from(pending, completed)
    }

    private fun toTodoResponse(todo: Todo): TodoResponse =
        TodoResponse.from(
            todo.id,
            todo.content,
            todo.dueDate,
            todo.isCompleted
        )
}
