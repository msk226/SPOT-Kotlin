package kr.spot.core.point.application.batch

import kr.spot.common.event.payload.NotificationEvent
import kr.spot.common.event.payload.NotificationType
import kr.spot.common.event.publisher.KafkaEventPublisher
import kr.spot.core.point.domain.PointStatus
import kr.spot.core.point.infrastrcuture.PointHistoryRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
@Transactional(readOnly = true)
@ConditionalOnProperty(
    prefix = "batch.scheduler.point-expiration-notify",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class PointExpirationNotifyScheduler(
    private val pointHistoryRepository: PointHistoryRepository,
    private val kafkaEventPublisher: KafkaEventPublisher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "\${batch.scheduler.point-expiration-notify.cron:0 0 9 * * *}")
    fun notifyPointExpiration() {
        val now = LocalDateTime.now()
        val targetDate = now.plusDays(7)

        log.info("[PointExpirationNotify] 알림 발송 시작 - 만료 예정일: {}", targetDate.toLocalDate())

        val expiringHistories = pointHistoryRepository.findAllByExpiredAtBetweenAndPointStatus(
            startDate = targetDate.toLocalDate().atStartOfDay(),
            endDate = targetDate.toLocalDate().atTime(23, 59, 59),
            pointStatus = PointStatus.ACTIVE
        )

        if (expiringHistories.isEmpty()) {
            log.info("[PointExpirationNotify] 만료 예정 포인트 없음")
            return
        }

        val expiringByMember = expiringHistories.groupBy { it.memberId }

        expiringByMember.forEach { (memberId, histories) ->
            val totalExpiringPoints = histories.sumOf { it.points }

            kafkaEventPublisher.publish(
                key = memberId.toString(),
                event = NotificationEvent(
                    targetMemberId = memberId,
                    title = "포인트 만료 예정 알림",
                    content = "${totalExpiringPoints}P가 ${targetDate.toLocalDate()}에 만료됩니다.",
                    notificationType = NotificationType.POINT_EXPIRATION,
                    referenceId = null
                )
            )
        }

        log.info("[PointExpirationNotify] 알림 발송 완료 - 대상 회원수: {}", expiringByMember.size)
    }
}
