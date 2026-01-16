package kr.spot.study.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.spot.common.api.exception.GeneralException
import kr.spot.common.api.status.ErrorStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

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
