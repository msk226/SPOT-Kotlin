package kr.spot.common.api.status

import kr.spot.common.api.BaseCode

enum class SuccessStatus(
    override val httpStatus: Int,
    override val code: String,
    override val message: String
) : BaseCode {

    OK(200, "COMMON200", "OK"),
    CREATED(201, "COMMON201", "생성 완료"),
    ACCEPTED(202, "COMMON202", "요청 수락됨"),
    NO_CONTENT(204, "COMMON204", "콘텐츠 없음"),
    ;

    override val isSuccess: Boolean = true
}
