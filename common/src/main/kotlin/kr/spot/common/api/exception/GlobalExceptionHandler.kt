package kr.spot.common.api.exception

import kr.spot.common.api.ApiResponse
import kr.spot.common.api.status.ErrorStatus
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(GeneralException::class)
    fun handleGeneralException(e: GeneralException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("GeneralException: {}", e.message)
        return ResponseEntity
            .status(e.status.httpStatus)
            .body(ApiResponse.failure(e.status))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Map<String, String>>> {
        val errors = e.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Invalid value") }
        log.warn("Validation failed: {}", errors)
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.failure(ErrorStatus.BAD_REQUEST.code, "입력값이 올바르지 않습니다.", errors))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("IllegalArgumentException: {}", e.message)
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.failure(ErrorStatus.BAD_REQUEST.code, e.message ?: "잘못된 요청입니다."))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Unit>> {
        log.error("Unhandled exception", e)
        return ResponseEntity
            .internalServerError()
            .body(ApiResponse.failure(ErrorStatus.INTERNAL_SERVER_ERROR))
    }
}
