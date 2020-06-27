package org.muellners.finscale.core.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Configuration
class RedisConfiguration() {
    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        log.info("Configuring Redis connection")
        return LettuceConnectionFactory()
    }
}
