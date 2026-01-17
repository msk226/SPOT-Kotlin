package kr.spot.core.point.infrastrcuture

import kr.spot.core.point.domain.PointHistory
import kr.spot.core.point.domain.PointStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface PointHistoryRepository : JpaRepository<PointHistory, Long> {
    fun existsByEventId(eventId: String): Boolean

    @Query(
        """
        SELECT ph FROM PointHistory ph
        WHERE ph.expiredAt < :now
        AND ph.pointStatus = :pointStatus
        """
    )
    fun findAllByExpiredAtBeforeAndPointStatus(
        @Param("now") now: LocalDateTime,
        @Param("pointStatus") pointStatus: PointStatus
    ): List<PointHistory>
}
