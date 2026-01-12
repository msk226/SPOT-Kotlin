package kr.spot.core.post.infrastructure.redis

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.Cursor
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

@Component
class PostViewFlushJob(
    private val redis: StringRedisTemplate,
    private val postViewFlusher: PostViewFlusher
) {
    @Scheduled(cron = "0 * * * * *")
    fun flush() {
        if (!tryAcquireLock()) {
            log.debug("다른 인스턴스에서 실행 중")
            return
        }

        try {
            executeBatch()
        } finally {
            releaseLock()
        }
    }

    private fun executeBatch() {
        log.info("PostViewFlushJob 시작")
        val startTime = System.currentTimeMillis()

        val result = processAllKeys()

        val duration = System.currentTimeMillis() - startTime
        log.info(
            "PostViewFlushJob 완료: 성공 {}건, 실패 {}건, {}ms",
            result.successCount,
            result.failureCount,
            duration
        )
    }

    private fun processAllKeys(): BatchResult {
        val result = BatchResult()

        val options =
            ScanOptions
                .scanOptions()
                .match(KEY_PATTERN)
                .count(500)
                .build()

        redis.execute(
            RedisCallback { connection ->
                try {
                    connection.scan(options).use { cursor: Cursor<ByteArray> ->
                        while (cursor.hasNext()) {
                            val keyBytes = cursor.next()
                            processKey(keyBytes, result)
                        }
                    }
                } catch (e: Exception) {
                    log.error("SCAN 처리 중 오류", e)
                }
                null
            }
        )

        return result
    }

    private fun processKey(
        keyBytes: ByteArray,
        result: BatchResult
    ) {
        val key = String(keyBytes, StandardCharsets.UTF_8)
        var delta = 0L

        try {
            // 1. GETDEL - 원자적으로 값을 읽고 삭제
            val valueStr = redis.opsForValue().getAndDelete(key) ?: return

            // 2. 값 파싱 및 검증
            delta = parseLong(valueStr)
            if (delta <= 0) {
                log.warn("유효하지 않은 값 무시: key={}, value={}", key, valueStr)
                return
            }

            val postId = extractPostIdFromKey(key)
            if (postId == null) {
                log.error("postId 추출 실패, 값 손실: key={}, delta={}", key, delta)
                return
            }

            // 3. DB 업데이트 (별도 트랜잭션)
            postViewFlusher.flushPostViews(postId, delta)

            result.recordSuccess()
            log.debug("처리 완료: postId={}, delta={}", postId, delta)
        } catch (e: Exception) {
            result.recordFailure()
            log.error("처리 실패: key={}", key, e)

            // GETDEL 이후 실패 시 값 복구하여 다음 배치에서 재시도
            if (delta > 0) {
                restoreValue(key, delta)
            }
        }
    }

    private fun restoreValue(
        key: String,
        delta: Long
    ) {
        try {
            redis.opsForValue().increment(key, delta)
            log.warn("DB 반영 실패, Redis에 값 복구 완료: key={}, delta={}", key, delta)
        } catch (e: Exception) {
            log.error("복구 실패, 데이터 손실: key={}, delta={}", key, delta, e)
        }
    }

    private fun tryAcquireLock(): Boolean {
        val acquired =
            redis
                .opsForValue()
                .setIfAbsent(LOCK_KEY, "1", 55, TimeUnit.SECONDS)
        return acquired == true
    }

    private fun releaseLock() {
        redis.delete(LOCK_KEY)
    }

    private fun parseLong(value: String): Long =
        try {
            value.toLong()
        } catch (e: NumberFormatException) {
            log.warn("Long 파싱 실패: {}", value)
            0L
        }

    private fun extractPostIdFromKey(key: String): Long? =
        try {
            key.substring(KEY_PREFIX.length).toLong()
        } catch (e: Exception) {
            log.error("postId 추출 실패: {}", key, e)
            null
        }

    private class BatchResult {
        var successCount: Int = 0
            private set
        var failureCount: Int = 0
            private set

        fun recordSuccess() {
            successCount++
        }

        fun recordFailure() {
            failureCount++
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(PostViewFlushJob::class.java)

        private const val KEY_PREFIX = "view:post:"
        private const val KEY_PATTERN = "$KEY_PREFIX*"
        private const val LOCK_KEY = "lock:view-flush"
    }
}
