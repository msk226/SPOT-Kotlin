package kr.spot.study.core.domain.enums

import kr.spot.common.api.exception.GeneralException
import kr.spot.common.api.status.ErrorStatus

enum class StudyMemberStatus {
    OWNER, // 스터디 장
    APPLIED, // 신청 승인 대기
    AWAITING_SELF_APPROVAL, // 본인 승인 대기
    APPROVED, // 신청 승인 완료
    SELF_REJECTED, // 본인 승인 거절
    REJECTED; // 신청 거절

    companion object {
        fun convert(status: String): StudyMemberStatus =
            entries.find { it.name == status }
                ?: throw GeneralException(ErrorStatus.NO_SUCH_STUDY_MEMBER_STATUS)
    }
}
