package kr.spot.study.core.infrastructure.batch

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class StudyViewFlushJob(
    private val redis: StringRedisTemplate,
    private val studyViewFlusher: StudyViewFlusher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 * * * * *")
    @SchedulerLock(name = "studyViewFlushJob", lockAtMostFor = "PT5M")
    fun flush() {
        executeBatch()
    }

    private fun executeBatch() {
        log.info("StudyViewFlushJob 시작")
        val startTime = System.currentTimeMillis()

        val result = processAllKeys()

        val duration = System.currentTimeMillis() - startTime
        log.info(
            "StudyViewFlushJob 완료: 성공 {}건, 실패 {}건, {}ms",
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
                .count(SCAN_COUNT)
                .build()

        redis.execute { connection ->
            try {
                connection.keyCommands().scan(options).use { cursor ->
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

        return result
    }

    private fun processKey(
        keyBytes: ByteArray,
        result: BatchResult
    ) {
        val key = String(keyBytes, StandardCharsets.UTF_8)
        var delta = 0L

        try {
            val valueStr = redis.opsForValue().getAndDelete(key) ?: return

            delta = parseLong(valueStr)
            if (delta <= 0) {
                log.warn("유효하지 않은 값 무시: key={}, value={}", key, valueStr)
                return
            }

            val studyId = extractStudyIdFromKey(key)
            if (studyId == null) {
                log.error("studyId 추출 실패, 값 손실: key={}, delta={}", key, delta)
                return
            }

            studyViewFlusher.updateViewCount(studyId, delta)
            result.recordSuccess()
            log.debug("처리 완료: studyId={}, delta={}", studyId, delta)
        } catch (e: Exception) {
            result.recordFailure()
            log.error("처리 실패: key={}", key, e)
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

    private fun parseLong(value: String): Long =
        try {
            value.toLong()
        } catch (e: NumberFormatException) {
            log.warn("Long 파싱 실패: {}", value)
            0L
        }

    private fun extractStudyIdFromKey(key: String): Long? =
        try {
            key.substring(KEY_PREFIX.length).toLong()
        } catch (e: Exception) {
            log.error("studyId 추출 실패: {}", key, e)
            null
        }

    private class BatchResult {
        var successCount = 0
        var failureCount = 0

        fun recordSuccess() {
            successCount++
        }

        fun recordFailure() {
            failureCount++
        }
    }

    companion object {
        private const val KEY_PREFIX = "view:delta:study:"
        private const val KEY_PATTERN = "$KEY_PREFIX*"
        private const val SCAN_COUNT = 500L
    }
}
