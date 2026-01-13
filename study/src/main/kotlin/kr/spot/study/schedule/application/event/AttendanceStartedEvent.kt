package kr.spot.study.schedule.application.event

data class AttendanceStartedEvent(
    val studyId: Long,
    val scheduleId: Long,
    val qrContent: String
) {
    companion object {
        fun of(
            studyId: Long,
            scheduleId: Long,
            qrContent: String
        ): AttendanceStartedEvent = AttendanceStartedEvent(studyId, scheduleId, qrContent)
    }
}
