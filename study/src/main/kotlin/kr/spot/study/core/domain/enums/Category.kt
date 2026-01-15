package kr.spot.study.core.domain.enums

import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.api.exception.GeneralException

enum class Category {
    LANGUAGE, // 어학
    CERTIFICATION, // 자격증
    CAREER, // 취업
    CURRENT_AFFAIRS, // 시사뉴스
    SELF_STUDY, // 자율학습
    DEBATE, // 토론
    PROJECT, // 프로젝트
    COMPETITION, // 공모전
    MAJOR_CAREER, // 전공및진로학습
    OTHER; // 기타

    companion object {
        fun contains(categoryName: String): Boolean = entries.any { it.name == categoryName }

        fun fromString(categoryName: String): Category =
            entries.firstOrNull { it.name == categoryName }
                ?: throw GeneralException(ErrorStatus.NO_SUCH_CATEGORY)
    }
}
