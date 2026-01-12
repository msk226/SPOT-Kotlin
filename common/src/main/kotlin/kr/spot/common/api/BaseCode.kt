package kr.spot.common.api

interface BaseCode {
    val httpStatus: Int
    val code: String
    val message: String
    val isSuccess: Boolean

    fun toReasonDto(): ReasonDto =
        ReasonDto(
            isSuccess = isSuccess,
            code = code,
            message = message,
            httpStatus = httpStatus
        )
}
