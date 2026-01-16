package kr.spot.study.core.presentation.query.dto.response

data class GetMyAppliedStudyResponse(
    val studies: List<MyAppliedStudy>
) {
    data class MyAppliedStudy(
        val applicationId: Long,
        val studyId: Long,
        val title: String,
        val profileImageUrl: String?
    ) {
        companion object {
            fun of(
                applicationId: Long,
                studyId: Long,
                title: String,
                profileImageUrl: String?
            ): MyAppliedStudy = MyAppliedStudy(applicationId, studyId, title, profileImageUrl)
        }
    }

    companion object {
        fun of(studies: List<MyAppliedStudy>): GetMyAppliedStudyResponse = GetMyAppliedStudyResponse(studies)
    }
}
