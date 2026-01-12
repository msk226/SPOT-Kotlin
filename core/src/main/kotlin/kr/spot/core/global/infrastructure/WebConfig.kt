package kr.spot.core.global.infrastructure

import kr.spot.core.global.interceptor.MemberIdInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val memberIdInterceptor: MemberIdInterceptor
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry
            .addInterceptor(memberIdInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                "/api/health",
                "/swagger-ui/**",
                "/v3/api-docs/**"
            )
    }
}
