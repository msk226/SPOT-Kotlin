package kr.spot.core.notification.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.exception.GeneralException
import kr.spot.core.global.domain.BaseEntity
import kr.spot.core.notification.domain.enums.NotificationType
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "notification")
@SQLDelete(sql = "UPDATE notification SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class Notification private constructor(
    @Id
    val id: Long,
    @Column(nullable = false)
    val memberId: Long,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: NotificationType,
    @Column(nullable = false)
    val notifierName: String,
    @Column(nullable = false)
    val studyId: Long,
    @Column(nullable = false)
    val studyTitle: String,
    @Column
    val studyProfileImage: String? = null,
    @Column
    val studyPostId: Long? = null,
    isChecked: Boolean
) : BaseEntity() {
    @Column(nullable = false)
    var isChecked: Boolean = isChecked
        private set

    fun markAsRead(memberId: Long) {
        checkAuthority(memberId)
        this.isChecked = true
    }

    private fun checkAuthority(memberId: Long) {
        if (memberId != this.memberId) {
            throw GeneralException(ErrorStatus.NOTIFICATION_ACCESS_DENIED)
        }
    }

    companion object {
        fun of(
            id: Long,
            memberId: Long,
            type: NotificationType,
            notifierName: String,
            studyId: Long,
            studyTitle: String,
            studyProfileImage: String? = null,
            studyPostId: Long? = null,
            isChecked: Boolean = false
        ): Notification =
            Notification(
                id = id,
                memberId = memberId,
                type = type,
                notifierName = notifierName,
                studyId = studyId,
                studyTitle = studyTitle,
                studyProfileImage = studyProfileImage,
                studyPostId = studyPostId,
                isChecked = isChecked
            )
    }
}
