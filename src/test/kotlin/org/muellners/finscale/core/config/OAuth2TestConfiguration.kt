package org.muellners.finscale.core.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer

@Configuration
@EnableResourceServer
class OAuth2TestConfiguration : ResourceServerConfigurer {
    // We set stateless to false to prevent the SecurityContext to be cleared when the respective filter is invoked
    // Otherwise it is impossible to use @WithMockUser in combination with oauth2 in tests
    override fun configure(security: ResourceServerSecurityConfigurer) {
        security.stateless(false)
    }

    override fun configure(http: HttpSecurity) { }
}
