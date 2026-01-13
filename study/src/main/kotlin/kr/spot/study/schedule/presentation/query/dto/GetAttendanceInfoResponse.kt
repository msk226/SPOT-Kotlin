package kr.spot.study.schedule.presentation.query.dto

data class GetAttendanceInfoResponse(
    val attendanceActive: Boolean,
    val qrCodeImageUrl: String?
) {
    companion object {
        fun of(
            attendanceActive: Boolean,
            qrCodeImageUrl: String?
        ): GetAttendanceInfoResponse = GetAttendanceInfoResponse(attendanceActive, qrCodeImageUrl)
    }
}
