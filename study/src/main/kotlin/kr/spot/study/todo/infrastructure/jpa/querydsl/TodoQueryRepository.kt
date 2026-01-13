package kr.spot.study.todo.infrastructure.jpa.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.spot.study.todo.domain.QTodo
import kr.spot.study.todo.domain.Todo
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class TodoQueryRepository(
    private val query: JPAQueryFactory
) {
    fun findByStudyIdAndMemberIdAndDueDate(
        studyId: Long,
        memberId: Long,
        dueDate: LocalDate
    ): List<Todo> {
        val todo = QTodo.todo

        return query
            .selectFrom(todo)
            .where(
                todo.studyId.eq(studyId),
                todo.memberId.eq(memberId),
                todo.dueDate.eq(dueDate)
            ).orderBy(
                todo.isCompleted.asc(),
                todo.createdAt.desc()
            ).fetch()
    }
}
