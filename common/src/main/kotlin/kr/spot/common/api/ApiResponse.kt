package kr.spot.common.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.api.status.SuccessStatus

@JsonPropertyOrder("isSuccess", "code", "message", "result")
data class ApiResponse<T>(
    @get:JsonProperty("isSuccess")
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val result: T? = null
) {
    companion object {
        // 성공 응답 (데이터 있음)
        fun <T> success(
            status: SuccessStatus,
            result: T
        ): ApiResponse<T> =
            ApiResponse(
                isSuccess = true,
                code = status.code,
                message = status.message,
                result = result
            )

        // 성공 응답 (데이터 없음)
        fun success(status: SuccessStatus): ApiResponse<Unit> =
            ApiResponse(
                isSuccess = true,
                code = status.code,
                message = status.message,
                result = null
            )

        // 성공 응답 - OK 기본값
        fun <T> ok(result: T): ApiResponse<T> = success(SuccessStatus.OK, result)

        fun ok(): ApiResponse<Unit> = success(SuccessStatus.OK)

        // 성공 응답 - CREATED
        fun <T> created(result: T): ApiResponse<T> = success(SuccessStatus.CREATED, result)

        fun created(): ApiResponse<Unit> = success(SuccessStatus.CREATED)

        // 실패 응답
        fun <T> failure(
            status: ErrorStatus,
            result: T? = null
        ): ApiResponse<T> =
            ApiResponse(
                isSuccess = false,
                code = status.code,
                message = status.message,
                result = result
            )

        // 실패 응답 (커스텀 메시지)
        fun <T> failure(
            code: String,
            message: String,
            result: T? = null
        ): ApiResponse<T> =
            ApiResponse(
                isSuccess = false,
                code = code,
                message = message,
                result = result
            )
    }
}
