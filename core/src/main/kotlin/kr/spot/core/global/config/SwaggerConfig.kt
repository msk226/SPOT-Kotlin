package kr.spot.core.global.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("SPOT Core API")
                    .description("회원 및 게시글 관리 서비스 API")
                    .version("1.0.0")
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "memberId",
                        SecurityScheme()
                            .type(SecurityScheme.Type.APIKEY)
                            .`in`(SecurityScheme.In.HEADER)
                            .name("memberId")
                            .description("회원 ID")
                    )
            )
            .addSecurityItem(SecurityRequirement().addList("memberId"))
}
