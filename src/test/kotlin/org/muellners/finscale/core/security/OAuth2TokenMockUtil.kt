package org.muellners.finscale.core.security

import java.util.UUID
import org.mockito.BDDMockito.given
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.request.RequestPostProcessor

/**
 * A bean providing simple mocking of OAuth2 access tokens for security integration tests.
 */
@Component
class OAuth2TokenMockUtil {

    @MockBean
    private lateinit var tokenServices: ResourceServerTokenServices

    private fun createAuthentication(username: String, scopes: Set<String>, roles: Set<String>): OAuth2Authentication {
        val authorities = roles.map { SimpleGrantedAuthority(it) }

        val principal = User(username, "test", true, true, true, true, authorities)
        val authentication = UsernamePasswordAuthenticationToken(
            principal, principal.password, principal.authorities
        )

        // Create the authorization request and OAuth2Authentication object
        val authRequest = OAuth2Request(null, "testClient", null, true, scopes, null, null, null, null)
        return OAuth2Authentication(authRequest, authentication)
    }

    @JvmOverloads
    fun oauth2Authentication(
        username: String,
        scopes: Set<String> = emptySet(),
        roles: Set<String> = emptySet()
    ): RequestPostProcessor {
        val uuid = UUID.randomUUID().toString()

        given(tokenServices.loadAuthentication(uuid))
            .willReturn(createAuthentication(username, scopes, roles))

        given(tokenServices.readAccessToken(uuid)).willReturn(DefaultOAuth2AccessToken(uuid))

        return OAuth2PostProcessor(uuid)
    }

    class OAuth2PostProcessor(private val token: String) : RequestPostProcessor {

        override fun postProcessRequest(mockHttpServletRequest: MockHttpServletRequest) =
            mockHttpServletRequest.apply { addHeader("Authorization", "Bearer $token") }
    }
}
