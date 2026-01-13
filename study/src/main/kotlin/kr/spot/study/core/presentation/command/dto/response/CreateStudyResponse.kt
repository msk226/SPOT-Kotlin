package kr.spot.study.core.presentation.command.dto.response

data class CreateStudyResponse(
    val studyId: Long
) {
    companion object {
        fun from(studyId: Long): CreateStudyResponse = CreateStudyResponse(studyId)
    }
}
