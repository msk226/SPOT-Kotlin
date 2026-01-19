package kr.spot.worker.admin.stream

import kr.spot.worker.admin.domain.AttendanceStats
import kr.spot.worker.admin.domain.HourlyStats
import kr.spot.worker.admin.domain.MemberStats
import kr.spot.worker.admin.domain.PointStats
import kr.spot.worker.admin.domain.RealtimeStats
import kr.spot.worker.admin.domain.StudyStats
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StoreQueryParameters
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyWindowStore
import org.slf4j.LoggerFactory
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class DashboardStatsService(
    private val streamsBuilderFactoryBean: StreamsBuilderFactoryBean
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun getRealtimeStats(date: LocalDate = LocalDate.now()): RealtimeStats {
        val dateKey = date.format(DateTimeFormatter.ISO_DATE)

        return RealtimeStats(
            date = date,
            memberStats = getMemberStats(dateKey),
            attendanceStats = getAttendanceStats(dateKey),
            pointStats = getPointStats(dateKey),
            studyStats = StudyStats() // TODO: Study 이벤트 구현 후 추가
        )
    }

    fun getHourlyStats(date: LocalDate = LocalDate.now()): List<HourlyStats> {
        val stats = mutableListOf<HourlyStats>()

        for (hour in 0..23) {
            val hourKey = "${date.format(DateTimeFormatter.ISO_DATE)}-$hour"
            val attendanceCount =
                getWindowedCount(
                    DashboardStatsProcessor.ATTENDANCE_HOURLY_COUNT_STORE,
                    hourKey
                )

            stats.add(
                HourlyStats(
                    hour = hour,
                    attendanceCount = attendanceCount
                )
            )
        }

        return stats
    }

    private fun getMemberStats(dateKey: String): MemberStats {
        val newMemberCount =
            getWindowedCount(
                DashboardStatsProcessor.MEMBER_DAILY_COUNT_STORE,
                dateKey
            )

        return MemberStats(newMemberCount = newMemberCount)
    }

    private fun getAttendanceStats(dateKey: String): AttendanceStats {
        val totalCount =
            getWindowedCount(
                DashboardStatsProcessor.ATTENDANCE_DAILY_COUNT_STORE,
                dateKey
            )

        // 시간대별 집계
        val byHour = mutableMapOf<Int, Long>()
        for (hour in 0..23) {
            val hourKey = "$dateKey-$hour"
            val count =
                getWindowedCount(
                    DashboardStatsProcessor.ATTENDANCE_HOURLY_COUNT_STORE,
                    hourKey
                )
            if (count > 0) {
                byHour[hour] = count
            }
        }

        return AttendanceStats(
            totalCount = totalCount,
            byHour = byHour
        )
    }

    private fun getPointStats(dateKey: String): PointStats {
        val grantCount =
            getWindowedCount(
                DashboardStatsProcessor.POINT_DAILY_COUNT_STORE,
                dateKey
            )
        val totalGranted =
            getWindowedCount(
                DashboardStatsProcessor.POINT_DAILY_SUM_STORE,
                dateKey
            )

        return PointStats(
            totalGranted = totalGranted,
            grantCount = grantCount
        )
    }

    private fun getWindowedCount(
        storeName: String,
        key: String
    ): Long {
        return try {
            val kafkaStreams =
                streamsBuilderFactoryBean.kafkaStreams
                    ?: return 0L

            if (kafkaStreams.state() != KafkaStreams.State.RUNNING) {
                log.debug("Kafka Streams is not running yet: {}", kafkaStreams.state())
                return 0L
            }

            val store: ReadOnlyWindowStore<String, Long> =
                kafkaStreams.store(
                    StoreQueryParameters.fromNameAndType(
                        storeName,
                        QueryableStoreTypes.windowStore()
                    )
                )

            // 오늘 날짜 기준으로 윈도우 범위 설정
            val now = Instant.now()
            val startOfDay =
                LocalDate
                    .now()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()

            var total = 0L
            store.fetch(key, startOfDay, now).forEach { kv ->
                total = kv.value ?: 0L
            }

            total
        } catch (e: Exception) {
            log.debug("Failed to query store {}: {}", storeName, e.message)
            0L
        }
    }
}
