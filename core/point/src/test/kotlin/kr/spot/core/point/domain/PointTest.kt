package kr.spot.core.point.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Point 도메인")
class PointTest {

    @Nested
    @DisplayName("create 메서드는")
    inner class Create {

        @Test
        fun `초기 amount가 0인 Point를 생성한다`() {
            // when
            val point = Point.create(id = 1L, memberId = 100L)

            // then
            assertThat(point.id).isEqualTo(1L)
            assertThat(point.memberId).isEqualTo(100L)
            assertThat(point.amount).isEqualTo(0L)
        }
    }

    @Nested
    @DisplayName("increaseAmount 메서드는")
    inner class IncreaseAmount {

        @Test
        fun `amount를 증가시킨다`() {
            // given
            val point = Point.create(id = 1L, memberId = 1L)

            // when
            point.increaseAmount(100L)

            // then
            assertThat(point.amount).isEqualTo(100L)
        }

        @Test
        fun `여러 번 호출 시 누적된다`() {
            // given
            val point = Point.create(id = 1L, memberId = 1L)

            // when
            point.increaseAmount(10L)
            point.increaseAmount(20L)
            point.increaseAmount(30L)

            // then
            assertThat(point.amount).isEqualTo(60L)
        }

        @Test
        fun `0을 더해도 amount가 변하지 않는다`() {
            // given
            val point = Point.create(id = 1L, memberId = 1L)
            point.increaseAmount(50L)

            // when
            point.increaseAmount(0L)

            // then
            assertThat(point.amount).isEqualTo(50L)
        }
    }
}
