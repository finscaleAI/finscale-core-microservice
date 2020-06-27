package org.muellners.finscale.core.client

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails

private const val AUTHORIZATION_HEADER = "Authorization"
private const val BEARER_TOKEN_TYPE = "Bearer"

class UserFeignClientInterceptor : RequestInterceptor {

    override fun apply(template: RequestTemplate) {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication

        if (authentication != null && authentication.details is OAuth2AuthenticationDetails) {
            val details = authentication.details as OAuth2AuthenticationDetails
            template.header(AUTHORIZATION_HEADER, "$BEARER_TOKEN_TYPE ${details.tokenValue}")
        }
    }
}
