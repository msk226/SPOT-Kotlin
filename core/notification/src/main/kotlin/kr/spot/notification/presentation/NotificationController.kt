package kr.spot.notification.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kr.spot.common.api.ApiResponse
import kr.spot.notification.application.NotificationQueryService
import kr.spot.notification.presentation.dto.response.NotificationListResponse
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "알림")
@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationQueryService: NotificationQueryService
) {
    @Operation(
        summary = "내 알림 전체 조회",
        description = """
            내게 생성된 알림을 전체 조회합니다.
            알림의 내용, 생성 시간, 알림의 종류, 알림을 생성한 스터디의 이름을 반환합니다.

            알림의 종류: ANNOUNCEMENT, SCHEDULE_UPDATE, TO_DO_UPDATE, POPULAR_POST
        """
    )
    @GetMapping
    fun getAllNotifications(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @Parameter(hidden = true) @RequestHeader memberId: Long
    ): ApiResponse<NotificationListResponse> {
        val response =
            notificationQueryService.getAllNotifications(
                memberId = memberId,
                pageable = PageRequest.of(page, size)
            )
        return ApiResponse.ok(response)
    }

    @Operation(
        summary = "알림 읽음 처리",
        description = "특정 알림을 읽음 처리합니다."
    )
    @GetMapping("{notificationId}/read")
    fun readNotification(
        @PathVariable notificationId: Long,
        @Parameter(hidden = true) @RequestHeader memberId: Long
    ): ApiResponse<Unit> {
        notificationQueryService.readNotification(
            memberId = memberId,
            notificationId = notificationId
        )
        return ApiResponse.ok()
    }
}
