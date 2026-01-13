package kr.spot.study.schedule.presentation.query.dto

import java.time.LocalDateTime

data class GetAttendanceListResponse(
    val attendances: List<AttendanceInfoResponse>,
    val totalCount: Int
) {
    companion object {
        fun from(
            attendances: List<AttendanceInfoResponse>,
            totalCount: Int
        ): GetAttendanceListResponse = GetAttendanceListResponse(attendances, totalCount)
    }

    data class AttendanceInfoResponse(
        val member: MemberInfoResponse,
        val attendanceStatus: String,
        val attendedAt: LocalDateTime?
    ) {
        companion object {
            fun from(
                member: MemberInfoResponse,
                attendanceStatus: String,
                attendedAt: LocalDateTime?
            ): AttendanceInfoResponse = AttendanceInfoResponse(member, attendanceStatus, attendedAt)
        }
    }

    data class MemberInfoResponse(
        val memberId: Long,
        val memberName: String,
        val memberProfileImageUrl: String?
    ) {
        companion object {
            fun from(
                memberId: Long,
                memberName: String,
                memberProfileImageUrl: String?
            ): MemberInfoResponse = MemberInfoResponse(memberId, memberName, memberProfileImageUrl)
        }
    }
}
