package kr.spot.study.core.infrastructure.redis

import kr.spot.common.view.ViewAbuseGuard
import kr.spot.common.view.ViewableType
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisViewAbuseGuard(
    val redis: StringRedisTemplate
) : ViewAbuseGuard {
    override fun shouldCount(
        type: ViewableType,
        targetId: Long,
        viewerId: Long
    ): Boolean {
        val key = guardKey(type, targetId, viewerId)
        val ok = redis.opsForValue().setIfAbsent(key, VALUE, WINDOW)
        return ok == true
    }

    companion object {
        private const val GUARD_KEY_FORMAT = "view:guard:%s:%d:%d"
        private const val VALUE = "1"
        private val WINDOW: Duration = Duration.ofMinutes(10)

        private fun guardKey(
            type: ViewableType,
            targetId: Long,
            viewerId: Long
        ): String = GUARD_KEY_FORMAT.format(type.keyPrefix, targetId, viewerId)
    }
}
