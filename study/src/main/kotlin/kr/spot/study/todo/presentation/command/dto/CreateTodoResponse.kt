package kr.spot.study.todo.presentation.command.dto

data class CreateTodoResponse(
    val todoId: Long
) {
    companion object {
        fun from(todoId: Long): CreateTodoResponse = CreateTodoResponse(todoId)
    }
}
