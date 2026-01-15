package kr.spot.study.todo.application.command

import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.api.exception.GeneralException
import kr.spot.common.id.IdGenerator
import kr.spot.study.todo.domain.Todo
import kr.spot.study.todo.infrastructure.jpa.TodoRepository
import kr.spot.study.todo.presentation.command.dto.CreateTodoRequest
import kr.spot.study.todo.presentation.command.dto.UpdateTodoRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ManageTodoService(
    private val idGenerator: IdGenerator,
    private val todoRepository: TodoRepository
) {
    fun createTodo(
        studyId: Long,
        memberId: Long,
        request: CreateTodoRequest
    ): Long {
        val todoId = idGenerator.nextId()
        val todo =
            Todo.of(
                todoId,
                studyId,
                memberId,
                request.dueDate,
                request.content
            )
        todoRepository.save(todo)
        return todoId
    }

    fun updateTodo(
        studyId: Long,
        todoId: Long,
        memberId: Long,
        request: UpdateTodoRequest
    ) {
        val todo = getById(todoId)
        todo.update(studyId, request.content, request.dueDate, memberId)
    }

    fun completeTodo(
        studyId: Long,
        todoId: Long,
        memberId: Long
    ) {
        val todo = getById(todoId)
        todo.complete(studyId, memberId)
    }

    fun uncompleteTodo(
        studyId: Long,
        todoId: Long,
        memberId: Long
    ) {
        val todo = getById(todoId)
        todo.uncomplete(studyId, memberId)
    }

    fun deleteTodo(
        studyId: Long,
        todoId: Long,
        memberId: Long
    ) {
        val todo = getById(todoId)
        todo.delete(studyId, memberId)
    }

    private fun getById(todoId: Long): Todo =
        todoRepository.findById(todoId).orElseThrow { GeneralException(ErrorStatus.TODO_NOT_FOUND) }
}
