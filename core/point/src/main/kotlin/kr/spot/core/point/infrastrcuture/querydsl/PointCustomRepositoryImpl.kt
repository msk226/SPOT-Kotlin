package kr.spot.core.point.infrastrcuture.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.spot.core.point.domain.QPointHistory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class PointCustomRepositoryImpl(
    private val query: JPAQueryFactory
) : PointCustomRepository {

    override fun getGainedPointsToday(memberId: Long, now: LocalDateTime): Long {
        val pointHistory = QPointHistory.pointHistory

        return (query.select(pointHistory.points.sum())
            .from(pointHistory)
            .where(
                pointHistory.memberId.eq(memberId),
                pointHistory.grantedAt.goe(now.toLocalDate().atStartOfDay()),
                pointHistory.grantedAt.loe(now)
            )
            .fetchOne() ?: 0L)
    }
}

