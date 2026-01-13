package kr.spot.study.core.infrastructure.jpa.dto

data class StudyApplicationInfo(
    val studyMemberId: Long,
    val studyId: Long,
    val studyName: String,
    val studyProfileImageUrl: String?
)
