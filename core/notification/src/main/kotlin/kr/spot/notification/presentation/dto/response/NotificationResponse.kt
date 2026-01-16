package kr.spot.notification.presentation.dto.response

import kr.spot.notification.domain.Notification
import kr.spot.notification.domain.enums.NotificationType
import java.time.LocalDateTime

data class NotificationListResponse(
    val notifications: List<NotificationResponse>,
    val totalCount: Long,
    val uncheckedCount: Long
) {
    companion object {
        fun of(
            notifications: List<NotificationResponse>,
            totalCount: Long,
            uncheckedCount: Long
        ): NotificationListResponse =
            NotificationListResponse(
                notifications = notifications,
                totalCount = totalCount,
                uncheckedCount = uncheckedCount
            )
    }
}

data class NotificationResponse(
    val notificationId: Long,
    val studyId: Long,
    val studyPostId: Long?,
    val studyTitle: String,
    val studyProfileImage: String?,
    val notifierName: String,
    val type: NotificationType,
    val isChecked: Boolean,
    val createdAt: LocalDateTime?
) {
    companion object {
        fun from(notification: Notification): NotificationResponse =
            NotificationResponse(
                notificationId = notification.id,
                studyId = notification.studyId,
                studyPostId = notification.studyPostId,
                studyTitle = notification.studyTitle,
                studyProfileImage = notification.studyProfileImage,
                notifierName = notification.notifierName,
                type = notification.type,
                isChecked = notification.isChecked,
                createdAt = notification.createdAt
            )
    }
}
