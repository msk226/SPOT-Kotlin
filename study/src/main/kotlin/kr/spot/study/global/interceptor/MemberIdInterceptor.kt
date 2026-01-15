package kr.spot.study.global.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.spot.common.api.exception.GeneralException
import kr.spot.common.api.status.ErrorStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

/**
 * 헤더에서 Member ID를 추출하여 Request Attribute에 저장하는 인터셉터
 *
 * 사용법:
 * - 요청 시 헤더에 X-Member-Id: {memberId} 추가
 * - 컨트롤러에서 @RequestAttribute("memberId") memberId: Long 으로 받기
 */
@Component
class MemberIdInterceptor : HandlerInterceptor {
    companion object {
        const val HEADER_NAME = "memberId"
        const val ATTRIBUTE_NAME = "memberId"
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val memberIdHeader =
            request.getHeader(HEADER_NAME)
                ?: throw GeneralException(ErrorStatus.UNAUTHORIZED)

        val memberId =
            memberIdHeader.toLongOrNull()
                ?: throw GeneralException(ErrorStatus.UNAUTHORIZED)

        request.setAttribute(ATTRIBUTE_NAME, memberId)
        return true
    }
}
