package kr.spot.core.point.infrastrcuture

import jakarta.persistence.LockModeType
import kr.spot.core.point.domain.Point
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock

interface PointRepository : JpaRepository<Point, Long> {
    fun findByMemberId(memberId: Long): Point?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findWithLockByMemberId(memberId: Long): Point?
}
