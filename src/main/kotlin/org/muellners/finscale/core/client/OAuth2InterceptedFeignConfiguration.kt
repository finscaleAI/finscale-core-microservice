package org.muellners.finscale.core.client

import io.github.jhipster.security.uaa.LoadBalancedResourceDetails
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext

class OAuth2InterceptedFeignConfiguration(private val loadBalancedResourceDetails: LoadBalancedResourceDetails) {

    @Bean(name = ["oauth2RequestInterceptor"])
    fun getOAuth2RequestInterceptor() =
        OAuth2FeignRequestInterceptor(DefaultOAuth2ClientContext(), loadBalancedResourceDetails)
}
