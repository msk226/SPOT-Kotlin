package kr.spot.worker.admin.domain

import java.time.Instant
import java.time.LocalDate

/**
 * 실시간 대시보드 통계
 */
data class RealtimeStats(
    val date: LocalDate,
    val memberStats: MemberStats,
    val attendanceStats: AttendanceStats,
    val pointStats: PointStats,
    val studyStats: StudyStats,
    val updatedAt: Instant = Instant.now()
)

data class MemberStats(
    val newMemberCount: Long = 0,
    val profileUpdateCount: Long = 0
)

data class AttendanceStats(
    val totalCount: Long = 0,
    val byHour: Map<Int, Long> = emptyMap()
)

data class PointStats(
    val totalGranted: Long = 0,
    val grantCount: Long = 0
)

data class StudyStats(
    val newStudyCount: Long = 0,
    val applicationCount: Long = 0,
    val memberJoinCount: Long = 0
)

/**
 * 시간대별 통계 (차트용)
 */
data class HourlyStats(
    val hour: Int,
    val memberCount: Long = 0,
    val attendanceCount: Long = 0,
    val pointGranted: Long = 0
)
