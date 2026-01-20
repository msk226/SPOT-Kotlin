package kr.spot.worker.admin.stream

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kr.spot.common.event.EventType
import kr.spot.common.event.Topics
import kr.spot.worker.admin.sse.DashboardStatsUpdatedEvent
import kr.spot.worker.admin.sse.StatsEventType
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Grouped
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.TimeWindows
import org.apache.kafka.streams.state.WindowStore
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class DashboardStatsProcessor(
    private val eventPublisher: ApplicationEventPublisher
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    companion object {
        // State Store 이름
        const val MEMBER_DAILY_COUNT_STORE = "member-daily-count"
        const val ATTENDANCE_DAILY_COUNT_STORE = "attendance-daily-count"
        const val ATTENDANCE_HOURLY_COUNT_STORE = "attendance-hourly-count"
        const val POINT_DAILY_SUM_STORE = "point-daily-sum"
        const val POINT_DAILY_COUNT_STORE = "point-daily-count"

        // 윈도우 설정
        private val DAILY_WINDOW = TimeWindows.ofSizeWithNoGrace(Duration.ofDays(1))
        private val HOURLY_WINDOW = TimeWindows.ofSizeWithNoGrace(Duration.ofHours(1))
    }

    @Autowired
    fun buildPipeline(streamsBuilder: StreamsBuilder) {
        processMemberEvents(streamsBuilder)
        processAttendanceEvents(streamsBuilder)
        processPointEvents(streamsBuilder)

        log.info("Dashboard stats pipeline initialized")
    }

    private fun processMemberEvents(builder: StreamsBuilder) {
        builder
            .stream<String, String>(Topics.MEMBER_EVENTS)
            .filter { _, value -> isEventType(value, EventType.MEMBER_CREATED) }
            .groupBy(
                { _, _ -> todayKey() },
                Grouped.with(Serdes.String(), Serdes.String())
            ).windowedBy(DAILY_WINDOW)
            .count(
                Materialized
                    .`as`<String, Long, WindowStore<Bytes, ByteArray>>(MEMBER_DAILY_COUNT_STORE)
                    .withKeySerde(Serdes.String())
                    .withValueSerde(Serdes.Long())
            ).toStream()
            .foreach { key, count ->
                log.debug("Member daily count updated: {} = {}", key.key(), count)
                publishEvent(StatsEventType.MEMBER_CREATED)
            }
    }

    private fun processAttendanceEvents(builder: StreamsBuilder) {
        val attendanceStream =
            builder
                .stream<String, String>(Topics.ATTENDANCE_EVENTS)
                .filter { _, value -> isEventType(value, EventType.ATTENDANCE_CHECKED) }

        // 일별 출석 수
        attendanceStream
            .groupBy(
                { _, _ -> todayKey() },
                Grouped.with(Serdes.String(), Serdes.String())
            ).windowedBy(DAILY_WINDOW)
            .count(
                Materialized
                    .`as`<String, Long, WindowStore<Bytes, ByteArray>>(ATTENDANCE_DAILY_COUNT_STORE)
                    .withKeySerde(Serdes.String())
                    .withValueSerde(Serdes.Long())
            ).toStream()
            .foreach { key, count ->
                log.debug("Attendance daily count updated: {} = {}", key.key(), count)
                publishEvent(StatsEventType.ATTENDANCE_CHECKED)
            }

        // 시간대별 출석 수
        attendanceStream
            .groupBy(
                { _, _ -> currentHourKey() },
                Grouped.with(Serdes.String(), Serdes.String())
            ).windowedBy(HOURLY_WINDOW)
            .count(
                Materialized
                    .`as`<String, Long, WindowStore<Bytes, ByteArray>>(ATTENDANCE_HOURLY_COUNT_STORE)
                    .withKeySerde(Serdes.String())
                    .withValueSerde(Serdes.Long())
            ).toStream()
            .foreach { key, count ->
                log.debug("Attendance hourly count updated: {} = {}", key.key(), count)
            }
    }

    private fun processPointEvents(builder: StreamsBuilder) {
        val pointStream =
            builder
                .stream<String, String>(Topics.POINT_EVENTS)
                .filter { _, value -> isEventType(value, EventType.POINT_GRANTED) }

        // 일별 포인트 지급 건수
        pointStream
            .groupBy(
                { _, _ -> todayKey() },
                Grouped.with(Serdes.String(), Serdes.String())
            ).windowedBy(DAILY_WINDOW)
            .count(
                Materialized
                    .`as`<String, Long, WindowStore<Bytes, ByteArray>>(POINT_DAILY_COUNT_STORE)
                    .withKeySerde(Serdes.String())
                    .withValueSerde(Serdes.Long())
            )

        // 일별 포인트 지급 합계
        pointStream
            .groupBy(
                { _, _ -> todayKey() },
                Grouped.with(Serdes.String(), Serdes.String())
            ).windowedBy(DAILY_WINDOW)
            .aggregate(
                { 0L },
                { _, value, aggregate -> aggregate + extractPoints(value) },
                Materialized
                    .`as`<String, Long, WindowStore<Bytes, ByteArray>>(POINT_DAILY_SUM_STORE)
                    .withKeySerde(Serdes.String())
                    .withValueSerde(Serdes.Long())
            ).toStream()
            .foreach { key, sum ->
                log.debug("Point daily sum updated: {} = {}", key.key(), sum)
                publishEvent(StatsEventType.POINT_GRANTED)
            }
    }

    private fun publishEvent(eventType: StatsEventType) {
        try {
            eventPublisher.publishEvent(DashboardStatsUpdatedEvent(eventType))
        } catch (e: Exception) {
            log.warn("Failed to publish SSE event: {}", e.message)
        }
    }

    private fun isEventType(
        json: String,
        eventType: EventType
    ): Boolean =
        try {
            val node: JsonNode = objectMapper.readTree(json)
            node.get("eventType")?.asText() == eventType.value
        } catch (e: Exception) {
            log.warn("Failed to parse event: {}", e.message)
            false
        }

    private fun extractPoints(json: String): Long =
        try {
            val node: JsonNode = objectMapper.readTree(json)
            node.get("points")?.asLong() ?: 0L
        } catch (e: Exception) {
            log.warn("Failed to extract points: {}", e.message)
            0L
        }

    private fun todayKey(): String = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

    private fun currentHourKey(): String {
        val now = java.time.LocalDateTime.now()
        return "${now.toLocalDate().format(DateTimeFormatter.ISO_DATE)}-${now.hour}"
    }
}
