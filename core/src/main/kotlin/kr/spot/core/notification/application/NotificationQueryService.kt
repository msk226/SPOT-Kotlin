package kr.spot.core.notification.application

import kr.spot.common.api.exception.GeneralException
import kr.spot.common.api.status.ErrorStatus
import kr.spot.core.notification.infrastructure.NotificationRepository
import kr.spot.core.notification.presentation.dto.response.NotificationListResponse
import kr.spot.core.notification.presentation.dto.response.NotificationResponse
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class NotificationQueryService(
    private val notificationRepository: NotificationRepository
) {
    fun getAllNotifications(
        memberId: Long,
        pageable: Pageable
    ): NotificationListResponse {
        val notifications =
            notificationRepository.findByMemberId(
                memberId = memberId,
                pageable = pageable
            )

        val responses = notifications.map { NotificationResponse.from(it) }
        val uncheckedCount = notificationRepository.countByMemberIdAndIsChecked(memberId, false)

        return NotificationListResponse.of(
            notifications = responses,
            totalCount = responses.size.toLong(),
            uncheckedCount = uncheckedCount
        )
    }

    fun readNotification(
        notificationId: Long,
        memberId: Long
    ) {
        val notification =
            notificationRepository
                .findById(notificationId)
                .orElseThrow({ GeneralException(ErrorStatus.NOTIFICATION_NOT_FOUND) })
        notification.markAsRead(memberId)
    }
}
