package kr.spot.core.point.application.batch

import kr.spot.core.point.domain.PointStatus
import kr.spot.core.point.infrastrcuture.PointHistoryRepository
import kr.spot.core.point.infrastrcuture.PointRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
@ConditionalOnProperty(
    prefix = "batch.scheduler.point-expiration",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class PointExpirationScheduler(
    private val pointRepository: PointRepository,
    private val pointHistoryRepository: PointHistoryRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "\${batch.scheduler.point-expiration.cron:0 0 0 * * *}")
    @Transactional
    fun expirePoints() {
        val now = LocalDateTime.now()
        log.info("[PointExpiration] 포인트 만료 처리 시작 - 기준시간: {}", now)

        val expiredHistories =
            pointHistoryRepository.findAllByExpiredAtBeforeAndPointStatus(
                now = now,
                pointStatus = PointStatus.ACTIVE
            )

        if (expiredHistories.isEmpty()) {
            log.info("[PointExpiration] 만료 대상 포인트 없음")
            return
        }

        log.info("[PointExpiration] 만료 대상 건수: {}", expiredHistories.size)

        val expiredByMember = expiredHistories.groupBy { it.memberId }

        expiredByMember.forEach { (memberId, histories) ->
            val totalExpiredPoints = histories.sumOf { it.points }

            val point = pointRepository.findWithLockByMemberId(memberId)
            if (point == null) {
                log.warn("[PointExpiration] Point 엔티티를 찾을 수 없음 - memberId: {}", memberId)
                return@forEach
            }

            // 포인트 차감 (잔액보다 많이 차감하지 않도록 보정)
            val actualDeduction = minOf(totalExpiredPoints, point.amount)
            if (actualDeduction > 0) {
                point.decreaseAmount(actualDeduction)
            }

            // PointHistory 상태 변경
            histories.forEach { it.expire() }

            log.info(
                "[PointExpiration] 회원 포인트 만료 처리 완료 - memberId: {}, 만료건수: {}, 차감포인트: {}",
                memberId,
                histories.size,
                actualDeduction
            )
        }

        log.info(
            "[PointExpiration] 포인트 만료 처리 완료 - 총 회원수: {}, 총 만료건수: {}",
            expiredByMember.size,
            expiredHistories.size
        )
    }
}
