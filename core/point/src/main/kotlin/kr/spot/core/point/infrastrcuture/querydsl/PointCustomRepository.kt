package kr.spot.core.point.infrastrcuture.querydsl

import java.time.LocalDateTime

interface PointCustomRepository {

    fun getGainedPointsToday(memberId: Long, now: LocalDateTime) : Long
}
