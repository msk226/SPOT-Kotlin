package kr.spot.core.point.application.batch

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kr.spot.common.event.contract.PointReason
import kr.spot.core.point.domain.Point
import kr.spot.core.point.domain.PointHistory
import kr.spot.core.point.domain.PointStatus
import kr.spot.core.point.infrastrcuture.PointHistoryRepository
import kr.spot.core.point.infrastrcuture.PointRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
@DisplayName("PointExpirationScheduler")
class PointExpirationSchedulerTest {
    @MockK
    private lateinit var pointRepository: PointRepository

    @MockK
    private lateinit var pointHistoryRepository: PointHistoryRepository

    @InjectMockKs
    private lateinit var sut: PointExpirationScheduler

    @Nested
    @DisplayName("expirePoints 메서드는")
    inner class ExpirePoints {
        @Test
        fun `만료 대상이 없으면 아무 작업도 하지 않는다`() {
            // given
            every {
                pointHistoryRepository.findAllByExpiredAtBeforeAndPointStatus(any(), any())
            } returns emptyList()

            // when
            sut.expirePoints()

            // then
            verify(exactly = 0) { pointRepository.findWithLockByMemberId(any()) }
        }

        @Test
        fun `만료된 포인트를 차감하고 상태를 EXPIRED로 변경한다`() {
            // given
            val memberId = 1L
            val point = Point.create(id = 1L, memberId = memberId)
            point.increaseAmount(100L)

            val expiredHistory =
                createPointHistory(
                    id = 1L,
                    memberId = memberId,
                    points = 50L,
                    grantedAt = LocalDateTime.now().minusYears(2)
                )

            every {
                pointHistoryRepository.findAllByExpiredAtBeforeAndPointStatus(any(), PointStatus.ACTIVE)
            } returns listOf(expiredHistory)
            every { pointRepository.findWithLockByMemberId(memberId) } returns point

            // when
            sut.expirePoints()

            // then
            assertThat(point.amount).isEqualTo(50L)
            assertThat(expiredHistory.pointStatus).isEqualTo(PointStatus.EXPIRED)
        }

        @Test
        fun `여러 회원의 만료 포인트를 각각 처리한다`() {
            // given
            val member1Id = 1L
            val member2Id = 2L

            val point1 = Point.create(id = 1L, memberId = member1Id)
            point1.increaseAmount(100L)

            val point2 = Point.create(id = 2L, memberId = member2Id)
            point2.increaseAmount(200L)

            val history1 =
                createPointHistory(
                    id = 1L,
                    memberId = member1Id,
                    points = 30L,
                    grantedAt = LocalDateTime.now().minusYears(2)
                )
            val history2 =
                createPointHistory(
                    id = 2L,
                    memberId = member2Id,
                    points = 50L,
                    grantedAt = LocalDateTime.now().minusYears(2)
                )

            every {
                pointHistoryRepository.findAllByExpiredAtBeforeAndPointStatus(any(), PointStatus.ACTIVE)
            } returns listOf(history1, history2)
            every { pointRepository.findWithLockByMemberId(member1Id) } returns point1
            every { pointRepository.findWithLockByMemberId(member2Id) } returns point2

            // when
            sut.expirePoints()

            // then
            assertThat(point1.amount).isEqualTo(70L)
            assertThat(point2.amount).isEqualTo(150L)
            assertThat(history1.pointStatus).isEqualTo(PointStatus.EXPIRED)
            assertThat(history2.pointStatus).isEqualTo(PointStatus.EXPIRED)
        }

        @Test
        fun `같은 회원의 여러 만료 포인트를 합산하여 차감한다`() {
            // given
            val memberId = 1L
            val point = Point.create(id = 1L, memberId = memberId)
            point.increaseAmount(100L)

            val history1 =
                createPointHistory(
                    id = 1L,
                    memberId = memberId,
                    points = 20L,
                    grantedAt = LocalDateTime.now().minusYears(2)
                )
            val history2 =
                createPointHistory(
                    id = 2L,
                    memberId = memberId,
                    points = 30L,
                    grantedAt = LocalDateTime.now().minusYears(2)
                )

            every {
                pointHistoryRepository.findAllByExpiredAtBeforeAndPointStatus(any(), PointStatus.ACTIVE)
            } returns listOf(history1, history2)
            every { pointRepository.findWithLockByMemberId(memberId) } returns point

            // when
            sut.expirePoints()

            // then
            assertThat(point.amount).isEqualTo(50L) // 100 - 20 - 30
            assertThat(history1.pointStatus).isEqualTo(PointStatus.EXPIRED)
            assertThat(history2.pointStatus).isEqualTo(PointStatus.EXPIRED)
        }

        @Test
        fun `잔액보다 만료 포인트가 많으면 잔액만큼만 차감한다`() {
            // given
            val memberId = 1L
            val point = Point.create(id = 1L, memberId = memberId)
            point.increaseAmount(30L) // 잔액 30

            val expiredHistory =
                createPointHistory(
                    id = 1L,
                    memberId = memberId,
                    points = 100L, // 만료 대상 100
                    grantedAt = LocalDateTime.now().minusYears(2)
                )

            every {
                pointHistoryRepository.findAllByExpiredAtBeforeAndPointStatus(any(), PointStatus.ACTIVE)
            } returns listOf(expiredHistory)
            every { pointRepository.findWithLockByMemberId(memberId) } returns point

            // when
            sut.expirePoints()

            // then
            assertThat(point.amount).isEqualTo(0L) // 30만 차감
            assertThat(expiredHistory.pointStatus).isEqualTo(PointStatus.EXPIRED)
        }

        @Test
        fun `Point 엔티티가 없는 회원은 건너뛴다`() {
            // given
            val memberId = 1L
            val expiredHistory =
                createPointHistory(
                    id = 1L,
                    memberId = memberId,
                    points = 50L,
                    grantedAt = LocalDateTime.now().minusYears(2)
                )

            every {
                pointHistoryRepository.findAllByExpiredAtBeforeAndPointStatus(any(), PointStatus.ACTIVE)
            } returns listOf(expiredHistory)
            every { pointRepository.findWithLockByMemberId(memberId) } returns null

            // when
            sut.expirePoints()

            // then
            assertThat(expiredHistory.pointStatus).isEqualTo(PointStatus.ACTIVE) // 변경되지 않음
        }

        @Test
        fun `잔액이 0인 경우 차감하지 않고 상태만 변경한다`() {
            // given
            val memberId = 1L
            val point = Point.create(id = 1L, memberId = memberId) // 잔액 0

            val expiredHistory =
                createPointHistory(
                    id = 1L,
                    memberId = memberId,
                    points = 50L,
                    grantedAt = LocalDateTime.now().minusYears(2)
                )

            every {
                pointHistoryRepository.findAllByExpiredAtBeforeAndPointStatus(any(), PointStatus.ACTIVE)
            } returns listOf(expiredHistory)
            every { pointRepository.findWithLockByMemberId(memberId) } returns point

            // when
            sut.expirePoints()

            // then
            assertThat(point.amount).isEqualTo(0L)
            assertThat(expiredHistory.pointStatus).isEqualTo(PointStatus.EXPIRED)
        }
    }

    private fun createPointHistory(
        id: Long,
        memberId: Long,
        points: Long,
        grantedAt: LocalDateTime
    ): PointHistory =
        PointHistory.of(
            id = id,
            eventId = "test-event-$id",
            memberId = memberId,
            points = points,
            reason = PointReason.ATTENDANCE,
            referenceId = null,
            grantedAt = grantedAt
        )
}
