package org.muellners.finscale.core.config

import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.cloud.openfeign.FeignClientsConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@EnableFeignClients(basePackages = ["org.muellners.finscale.core"])
@Import(FeignClientsConfiguration::class)
class FeignConfiguration {

    /**
     * Set the Feign specific log level to log client REST requests.
     */
    @Bean
    internal fun feignLoggerLevel() = feign.Logger.Level.BASIC
}
