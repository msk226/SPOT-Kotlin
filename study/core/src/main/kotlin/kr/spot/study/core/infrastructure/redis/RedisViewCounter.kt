package kr.spot.study.core.infrastructure.redis

import kr.spot.common.view.ViewCounter
import kr.spot.common.view.ViewableType
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisViewCounter(
    val redis: StringRedisTemplate
) : ViewCounter {
    override fun incrementAndGet(
        type: ViewableType,
        targetId: Long
    ): Long {
        val v = redis.opsForValue().increment(deltaKey(type, targetId))
        return v ?: 0L
    }

    override fun currentDelta(
        type: ViewableType,
        targetId: Long
    ): Long {
        val v = redis.opsForValue().get(deltaKey(type, targetId))
        return v?.toLongOrNull() ?: 0L
    }

    companion object {
        private const val DELTA_KEY_FORMAT = "view:delta:%s:%d"

        fun deltaKey(
            type: ViewableType,
            targetId: Long
        ): String = DELTA_KEY_FORMAT.format(type.keyPrefix, targetId)
    }
}
