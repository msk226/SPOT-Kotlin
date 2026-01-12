package kr.spot.common.config

import kr.spot.common.id.IdGenerator
import kr.spot.common.id.Snowflake
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class IdGeneratorConfig {
    @Bean
    fun idGenerator(): IdGenerator = Snowflake()
}
