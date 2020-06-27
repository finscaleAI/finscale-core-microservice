package org.muellners.finscale.core.client

import java.io.IOException
import org.springframework.context.annotation.Bean

class OAuth2UserClientFeignConfiguration {

    @Bean(name = ["userFeignClientInterceptor"])
    @Throws(IOException::class)
    fun getUserFeignClientInterceptor() = UserFeignClientInterceptor()
}
