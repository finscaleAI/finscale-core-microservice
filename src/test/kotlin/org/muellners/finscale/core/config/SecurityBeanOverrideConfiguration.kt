package org.muellners.finscale.core.config

import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.web.client.RestTemplate

/**
 * Overrides UAA specific beans, so they do not interfere the testing.
 * This configuration must be included in `@SpringBootTest` in order to take effect.
 */
@Configuration
class SecurityBeanOverrideConfiguration {

    @Bean
    @Primary
    fun tokenStore(): TokenStore? = null

    @Bean
    @Primary
    fun jwtAccessTokenConverter(): JwtAccessTokenConverter? = null

    @Bean
    @Primary
    fun loadBalancedRestTemplate(customizer: RestTemplateCustomizer): RestTemplate? = null
}
