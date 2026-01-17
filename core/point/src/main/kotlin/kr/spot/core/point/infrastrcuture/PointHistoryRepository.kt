package kr.spot.core.point.infrastrcuture

import kr.spot.core.point.domain.PointHistory
import org.springframework.data.jpa.repository.JpaRepository

interface PointHistoryRepository : JpaRepository<PointHistory, Long> {
    fun existsByEventId(eventId: String): Boolean
    fun id(id: Long): MutableList<PointHistory>
}
