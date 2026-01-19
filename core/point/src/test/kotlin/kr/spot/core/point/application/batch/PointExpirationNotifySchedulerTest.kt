package kr.spot.core.point.application.batch

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import kr.spot.common.event.DomainEvent
import kr.spot.common.event.contract.PointReason
import kr.spot.common.event.payload.NotificationEvent
import kr.spot.common.event.payload.NotificationType
import kr.spot.common.event.publisher.KafkaEventPublisher
import kr.spot.core.point.domain.PointHistory
import kr.spot.core.point.domain.PointStatus
import kr.spot.core.point.infrastrcuture.PointHistoryRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
@DisplayName("PointExpirationNotifyScheduler")
class PointExpirationNotifySchedulerTest {
    @MockK
    private lateinit var pointHistoryRepository: PointHistoryRepository

    @MockK(relaxed = true)
    private lateinit var kafkaEventPublisher: KafkaEventPublisher

    @InjectMockKs
    private lateinit var sut: PointExpirationNotifyScheduler

    @Nested
    @DisplayName("notifyPointExpiration 메서드는")
    inner class NotifyPointExpiration {
        @Test
        fun `만료 예정 포인트가 없으면 알림을 발송하지 않는다`() {
            // given
            every {
                pointHistoryRepository.findAllByExpiredAtBetweenAndPointStatus(any(), any(), any())
            } returns emptyList()

            // when
            sut.notifyPointExpiration()

            // then
            verify(exactly = 0) { kafkaEventPublisher.publish(any<String>(), any<DomainEvent>()) }
        }

        @Test
        fun `만료 예정 포인트가 있으면 해당 회원에게 알림을 발송한다`() {
            // given
            val memberId = 1L
            val expiringHistory =
                createPointHistory(
                    id = 1L,
                    memberId = memberId,
                    points = 100L
                )

            every {
                pointHistoryRepository.findAllByExpiredAtBetweenAndPointStatus(any(), any(), PointStatus.ACTIVE)
            } returns listOf(expiringHistory)

            val keySlot = slot<String>()
            val eventSlot = slot<NotificationEvent>()

            every {
                kafkaEventPublisher.publish(capture(keySlot), capture(eventSlot))
            } returns Unit

            // when
            sut.notifyPointExpiration()

            // then
            verify(exactly = 1) { kafkaEventPublisher.publish(any<String>(), any<DomainEvent>()) }
            assertThat(keySlot.captured).isEqualTo(memberId.toString())
            assertThat(eventSlot.captured.targetMemberId).isEqualTo(memberId)
            assertThat(eventSlot.captured.notificationType).isEqualTo(NotificationType.POINT_EXPIRATION)
            assertThat(eventSlot.captured.content).contains("100P")
        }

        @Test
        fun `같은 회원의 여러 만료 예정 포인트를 합산하여 알림을 발송한다`() {
            // given
            val memberId = 1L
            val history1 = createPointHistory(id = 1L, memberId = memberId, points = 50L)
            val history2 = createPointHistory(id = 2L, memberId = memberId, points = 30L)

            every {
                pointHistoryRepository.findAllByExpiredAtBetweenAndPointStatus(any(), any(), PointStatus.ACTIVE)
            } returns listOf(history1, history2)

            val eventSlot = slot<NotificationEvent>()

            every {
                kafkaEventPublisher.publish(any<String>(), capture(eventSlot))
            } returns Unit

            // when
            sut.notifyPointExpiration()

            // then
            verify(exactly = 1) { kafkaEventPublisher.publish(any<String>(), any<DomainEvent>()) }
            assertThat(eventSlot.captured.content).contains("80P") // 50 + 30
        }

        @Test
        fun `여러 회원에게 각각 알림을 발송한다`() {
            // given
            val member1Id = 1L
            val member2Id = 2L
            val history1 = createPointHistory(id = 1L, memberId = member1Id, points = 100L)
            val history2 = createPointHistory(id = 2L, memberId = member2Id, points = 200L)

            every {
                pointHistoryRepository.findAllByExpiredAtBetweenAndPointStatus(any(), any(), PointStatus.ACTIVE)
            } returns listOf(history1, history2)

            val keySlots = mutableListOf<String>()
            val eventSlots = mutableListOf<NotificationEvent>()

            every {
                kafkaEventPublisher.publish(capture(keySlots), capture(eventSlots))
            } returns Unit

            // when
            sut.notifyPointExpiration()

            // then
            verify(exactly = 2) { kafkaEventPublisher.publish(any<String>(), any<DomainEvent>()) }
            assertThat(keySlots).containsExactlyInAnyOrder(
                member1Id.toString(),
                member2Id.toString()
            )
        }

        @Test
        fun `알림 제목이 포인트 만료 예정 알림으로 설정된다`() {
            // given
            val memberId = 1L
            val history = createPointHistory(id = 1L, memberId = memberId, points = 100L)

            every {
                pointHistoryRepository.findAllByExpiredAtBetweenAndPointStatus(any(), any(), PointStatus.ACTIVE)
            } returns listOf(history)

            val eventSlot = slot<NotificationEvent>()

            every {
                kafkaEventPublisher.publish(any<String>(), capture(eventSlot))
            } returns Unit

            // when
            sut.notifyPointExpiration()

            // then
            assertThat(eventSlot.captured.title).isEqualTo("포인트 만료 예정 알림")
        }

        @Test
        fun `memberId를 Kafka key로 사용한다`() {
            // given
            val memberId = 12345L
            val history = createPointHistory(id = 1L, memberId = memberId, points = 100L)

            every {
                pointHistoryRepository.findAllByExpiredAtBetweenAndPointStatus(any(), any(), PointStatus.ACTIVE)
            } returns listOf(history)

            val keySlot = slot<String>()

            every {
                kafkaEventPublisher.publish(capture(keySlot), any<DomainEvent>())
            } returns Unit

            // when
            sut.notifyPointExpiration()

            // then
            assertThat(keySlot.captured).isEqualTo("12345")
        }
    }

    private fun createPointHistory(
        id: Long,
        memberId: Long,
        points: Long
    ): PointHistory =
        PointHistory.of(
            id = id,
            eventId = "test-event-$id",
            memberId = memberId,
            points = points,
            reason = PointReason.ATTENDANCE,
            referenceId = null,
            grantedAt = LocalDateTime.now().minusMonths(11) // 1개월 후 만료 예정
        )
}
