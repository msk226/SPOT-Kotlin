package kr.spot.study.schedule.presentation.query.dto

data class GetAttendanceInfoResponse(
    val attendanceActive: Boolean,
    val attendanceCode: String?
) {
    companion object {
        fun of(
            attendanceActive: Boolean,
            attendanceCode: String?
        ): GetAttendanceInfoResponse = GetAttendanceInfoResponse(attendanceActive, attendanceCode)
    }
}
