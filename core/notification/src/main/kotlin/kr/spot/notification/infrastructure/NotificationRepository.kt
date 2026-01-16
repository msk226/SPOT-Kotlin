package kr.spot.notification.infrastructure

import kr.spot.notification.domain.Notification
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByMemberId(
        memberId: Long,
        pageable: Pageable
    ): List<Notification>

    fun countByMemberIdAndIsChecked(
        memberId: Long,
        isChecked: Boolean
    ): Long
}
