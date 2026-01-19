package kr.spot.worker.admin.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.common.event.contract.PointReason
import kr.spot.common.event.contract.StreakMileStone
import kr.spot.common.event.payload.AttendanceCheckedEvent
import kr.spot.common.event.payload.MemberCreatedEvent
import kr.spot.common.event.payload.PointGrantedEvent
import kr.spot.common.event.payload.PointIssuer
import kr.spot.common.event.publisher.KafkaEventPublisher
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import kotlin.random.Random

@Tag(name = "Dashboard Simulator", description = "대시보드 테스트용 이벤트 시뮬레이터 API")
@RestController
@RequestMapping("/api/admin/simulator")
class DashboardSimulatorController(
    private val kafkaEventPublisher: KafkaEventPublisher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "회원 가입 이벤트 발행", description = "테스트용 회원 가입 이벤트를 발행합니다")
    @PostMapping("/member/signup")
    fun simulateMemberSignup(
        @RequestBody request: MemberSignupRequest
    ): ApiResponse<SimulationResult> {
        val events =
            (1..request.count).map {
                MemberCreatedEvent(memberId = generateMemberId())
            }

        events.forEach { event ->
            kafkaEventPublisher.publish(event)
        }

        log.info("[Simulator] Published {} MemberCreatedEvent(s)", request.count)

        return ApiResponse.ok(
            SimulationResult(
                eventType = "MEMBER_CREATED",
                count = request.count,
                message = "${request.count}건의 회원 가입 이벤트가 발행되었습니다"
            )
        )
    }

    @Operation(summary = "출석 체크 이벤트 발행", description = "테스트용 출석 체크 이벤트를 발행합니다")
    @PostMapping("/attendance/check")
    fun simulateAttendanceCheck(
        @RequestBody request: AttendanceCheckRequest
    ): ApiResponse<SimulationResult> {
        val events =
            (1..request.count).map {
                val streak = Random.nextInt(1, 30)
                AttendanceCheckedEvent(
                    memberId = generateMemberId(),
                    checkedDate = LocalDate.now(),
                    currentStreak = streak,
                    milestone =
                        when {
                            streak == 7 -> StreakMileStone.ONE_WEEK
                            streak == 14 -> StreakMileStone.TWO_WEEKS
                            else -> null
                        }
                )
            }

        events.forEach { event ->
            kafkaEventPublisher.publish(event)
        }

        log.info("[Simulator] Published {} AttendanceCheckedEvent(s)", request.count)

        return ApiResponse.ok(
            SimulationResult(
                eventType = "ATTENDANCE_CHECKED",
                count = request.count,
                message = "${request.count}건의 출석 체크 이벤트가 발행되었습니다"
            )
        )
    }

    @Operation(summary = "포인트 지급 이벤트 발행", description = "테스트용 포인트 지급 이벤트를 발행합니다")
    @PostMapping("/point/grant")
    fun simulatePointGrant(
        @RequestBody request: PointGrantRequest
    ): ApiResponse<SimulationResult> {
        val pointMap =
            mapOf(
                PointReason.ATTENDANCE to 10L,
                PointReason.ATTENDANCE_STREAK_FOR_7_DAYS to 50L,
                PointReason.ATTENDANCE_STREAK_FOR_14_DAYS to 100L,
                PointReason.MINORITY_GAME_WIN to 30L,
                PointReason.POKE to 5L,
                PointReason.POKE_RECEIVED to 5L
            )

        val events =
            (1..request.count).map {
                val reason = pointMap.keys.random()
                PointGrantedEvent(
                    memberId = generateMemberId(),
                    points = pointMap[reason] ?: 10L,
                    reason = reason,
                    issuer = PointIssuer.CORE,
                    referenceId = null
                )
            }

        events.forEach { event ->
            kafkaEventPublisher.publish(event)
        }

        val totalPoints = events.sumOf { it.points }
        log.info("[Simulator] Published {} PointGrantedEvent(s), total: {}P", request.count, totalPoints)

        return ApiResponse.ok(
            SimulationResult(
                eventType = "POINT_GRANTED",
                count = request.count,
                message = "${request.count}건의 포인트 지급 이벤트가 발행되었습니다 (총 ${totalPoints}P)"
            )
        )
    }

    @Operation(
        summary = "대시보드 시뮬레이션 (복합)",
        description = "회원가입 + 출석 + 포인트 이벤트를 한번에 발행합니다"
    )
    @PostMapping("/dashboard/simulate")
    fun simulateDashboard(
        @RequestBody request: DashboardSimulationRequest
    ): ApiResponse<DashboardSimulationResult> {
        // 회원 가입
        val memberEvents =
            (1..request.memberCount).map {
                MemberCreatedEvent(memberId = generateMemberId())
            }
        memberEvents.forEach { kafkaEventPublisher.publish(it) }

        // 출석 체크
        val attendanceEvents =
            (1..request.attendanceCount).map {
                val streak = Random.nextInt(1, 30)
                AttendanceCheckedEvent(
                    memberId = generateMemberId(),
                    checkedDate = LocalDate.now(),
                    currentStreak = streak,
                    milestone =
                        when {
                            streak == 7 -> StreakMileStone.ONE_WEEK
                            streak == 14 -> StreakMileStone.TWO_WEEKS
                            else -> null
                        }
                )
            }
        attendanceEvents.forEach { kafkaEventPublisher.publish(it) }

        // 포인트 지급
        val pointMap =
            mapOf(
                PointReason.ATTENDANCE to 10L,
                PointReason.ATTENDANCE_STREAK_FOR_7_DAYS to 50L,
                PointReason.ATTENDANCE_STREAK_FOR_14_DAYS to 100L
            )
        val pointEvents =
            (1..request.pointCount).map {
                val reason = pointMap.keys.random()
                PointGrantedEvent(
                    memberId = generateMemberId(),
                    points = pointMap[reason] ?: 10L,
                    reason = reason,
                    issuer = PointIssuer.CORE,
                    referenceId = null
                )
            }
        pointEvents.forEach { kafkaEventPublisher.publish(it) }

        val totalPoints = pointEvents.sumOf { it.points }

        log.info(
            "[Simulator] Dashboard simulation completed - members: {}, attendance: {}, points: {} ({}P)",
            request.memberCount,
            request.attendanceCount,
            request.pointCount,
            totalPoints
        )

        return ApiResponse.ok(
            DashboardSimulationResult(
                memberCount = request.memberCount,
                attendanceCount = request.attendanceCount,
                pointCount = request.pointCount,
                totalPoints = totalPoints,
                message = "시뮬레이션이 완료되었습니다"
            )
        )
    }

    private fun generateMemberId(): Long = Random.nextLong(100000, 999999)
}

// Request DTOs
data class MemberSignupRequest(
    val count: Int = 1
)

data class AttendanceCheckRequest(
    val count: Int = 1
)

data class PointGrantRequest(
    val count: Int = 1
)

data class DashboardSimulationRequest(
    val memberCount: Int = 10,
    val attendanceCount: Int = 50,
    val pointCount: Int = 50
)

// Response DTOs
data class SimulationResult(
    val eventType: String,
    val count: Int,
    val message: String
)

data class DashboardSimulationResult(
    val memberCount: Int,
    val attendanceCount: Int,
    val pointCount: Int,
    val totalPoints: Long,
    val message: String
)
