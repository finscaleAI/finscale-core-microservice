package org.muellners.finscale.core.config.oauth2

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * OAuth2 properties define properties for OAuth2-based microservices.
 */
@Component
@ConfigurationProperties(prefix = "oauth2", ignoreUnknownFields = false)
class OAuth2Properties {
    val webClientConfiguration = WebClientConfiguration()

    val signatureVerification = SignatureVerification()

    class WebClientConfiguration {
        var clientId: String? = "web_app"
        var secret: String? = "changeit"
        /**
         * Holds the session timeout in seconds for non-remember-me sessions.
         * After so many seconds of inactivity, the session will be terminated.
         * Only checked during token refresh, so long access token validity may
         * delay the session timeout accordingly.
         */
        var sessionTimeoutInSeconds: Int? = 1800
        /**
         * Defines the cookie domain. If specified, cookies will be set on this domain.
         * If not configured, then cookies will be set on the top-level domain of the
         * request you sent, i.e. if you send a request to `app1.your-domain.com`,
         * then cookies will be set on `.your-domain.com`, such that they
         * are also valid for `app2.your-domain.com`.
         */
        var cookieDomain: String? = null
    }

    class SignatureVerification {
        /**
         * Maximum refresh rate for public keys in ms.
         * We won't fetch new public keys any faster than that to avoid spamming UAA in case
         * we receive a lot of "illegal" tokens.
         */
        var publicKeyRefreshRateLimit: Long? = 10 * 1000L
        /**
         * Maximum TTL for the public key in ms.
         * The public key will be fetched again from UAA if it gets older than that.
         * That way, we make sure that we get the newest keys always in case they are updated there.
         */
        var ttl: Long? = 24 * 60 * 60 * 1000L
        /**
         * Endpoint where to retrieve the public key used to verify token signatures.
         */
        var publicKeyEndpointUri: String? = "http://uaa/oauth/token_key"
    }
}
