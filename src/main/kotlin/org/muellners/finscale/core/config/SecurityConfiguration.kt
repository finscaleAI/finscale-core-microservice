package org.muellners.finscale.core.config

import org.muellners.finscale.core.config.oauth2.OAuth2JwtAccessTokenConverter
import org.muellners.finscale.core.config.oauth2.OAuth2Properties
import org.muellners.finscale.core.security.ADMIN
import org.muellners.finscale.core.security.oauth2.OAuth2SignatureVerifierClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import org.springframework.web.client.RestTemplate

@Configuration
@EnableResourceServer
class SecurityConfiguration(private val oAuth2Properties: OAuth2Properties) : ResourceServerConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .csrf()
            .disable()
            .headers()
            .frameOptions()
            .disable()
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .authorizeRequests()
            .antMatchers("/api/**").authenticated()
            .antMatchers("/management/health").permitAll()
            .antMatchers("/management/info").permitAll()
            .antMatchers("/management/prometheus").permitAll()
            .antMatchers("/management/**").hasAuthority(ADMIN)
    }

    @Bean
    fun tokenStore(jwtAccessTokenConverter: JwtAccessTokenConverter) = JwtTokenStore(jwtAccessTokenConverter)

    @Bean
    fun jwtAccessTokenConverter(signatureVerifierClient: OAuth2SignatureVerifierClient) =
        OAuth2JwtAccessTokenConverter(oAuth2Properties, signatureVerifierClient)

    @Bean
    @Qualifier("loadBalancedRestTemplate")
    fun loadBalancedRestTemplate(customizer: RestTemplateCustomizer) =
        RestTemplate().apply { customizer.customize(this) }
}
