package kr.spot.core.point.infrastrcuture

import kr.spot.core.point.domain.Point
import org.springframework.data.jpa.repository.JpaRepository

interface PointRepository : JpaRepository<Point, Long> {
}
