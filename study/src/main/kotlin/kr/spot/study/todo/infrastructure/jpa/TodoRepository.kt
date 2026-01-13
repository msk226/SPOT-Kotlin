package kr.spot.study.todo.infrastructure.jpa

import kr.spot.study.todo.domain.Todo
import org.springframework.data.jpa.repository.JpaRepository

interface TodoRepository : JpaRepository<Todo, Long>
