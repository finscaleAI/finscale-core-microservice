package org.muellners.finscale.core.web.rest.errors

import org.junit.jupiter.api.Test
import org.muellners.finscale.core.CoreApp
import org.muellners.finscale.core.config.SecurityBeanOverrideConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Integration tests [ExceptionTranslator] controller advice.
 */
@WithMockUser
@AutoConfigureMockMvc
@SpringBootTest(classes = [SecurityBeanOverrideConfiguration::class, CoreApp::class])
class ExceptionTranslatorIT {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @Throws(Exception::class)
    fun testConcurrencyFailure() {
        mockMvc.perform(get("/api/exception-translator-test/concurrency-failure").with(csrf()))
            .andExpect(status().isConflict)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("\$.message").value(ERR_CONCURRENCY_FAILURE))
    }

    @Test
    @Throws(Exception::class)
    fun testMethodArgumentNotValid() {
        mockMvc.perform(post("/api/exception-translator-test/method-argument").content("{}").contentType(MediaType.APPLICATION_JSON).with(csrf()))
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("\$.message").value(ERR_VALIDATION))
            .andExpect(jsonPath("\$.fieldErrors.[0].objectName").value("test"))
            .andExpect(jsonPath("\$.fieldErrors.[0].field").value("test"))
            .andExpect(jsonPath("\$.fieldErrors.[0].message").value("NotNull"))
    }

    @Test
    @Throws(Exception::class)
    fun testMissingServletRequestPartException() {
        mockMvc.perform(get("/api/exception-translator-test/missing-servlet-request-part").with(csrf()))
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("\$.message").value("error.http.400"))
    }

    @Test
    @Throws(Exception::class)
    fun testMissingServletRequestParameterException() {
        mockMvc.perform(get("/api/exception-translator-test/missing-servlet-request-parameter").with(csrf()))
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("\$.message").value("error.http.400"))
    }

    @Test
    @Throws(Exception::class)
    fun testAccessDenied() {
        mockMvc.perform(get("/api/exception-translator-test/access-denied").with(csrf()))
            .andExpect(status().isForbidden)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("\$.message").value("error.http.403"))
            .andExpect(jsonPath("\$.detail").value("test access denied!"))
    }

    @Test
    @Throws(Exception::class)
    fun testUnauthorized() {
        mockMvc.perform(get("/api/exception-translator-test/unauthorized").with(csrf()))
            .andExpect(status().isUnauthorized)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("\$.message").value("error.http.401"))
            .andExpect(jsonPath("\$.path").value("/api/exception-translator-test/unauthorized"))
            .andExpect(jsonPath("\$.detail").value("test authentication failed!"))
    }

    @Test
    @Throws(Exception::class)
    fun testMethodNotSupported() {
        mockMvc.perform(post("/api/exception-translator-test/access-denied").with(csrf()))
            .andExpect(status().isMethodNotAllowed)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("\$.message").value("error.http.405"))
            .andExpect(jsonPath("\$.detail").value("Request method 'POST' not supported"))
    }

    @Test
    @Throws(Exception::class)
    fun testExceptionWithResponseStatus() {
        mockMvc.perform(get("/api/exception-translator-test/response-status").with(csrf()))
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("\$.message").value("error.http.400"))
            .andExpect(jsonPath("\$.title").value("test response status"))
    }

    @Test
    @Throws(Exception::class)
    fun testInternalServerError() {
        mockMvc.perform(get("/api/exception-translator-test/internal-server-error").with(csrf()))
            .andExpect(status().isInternalServerError)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("\$.message").value("error.http.500"))
            .andExpect(jsonPath("\$.title").value("Internal Server Error"))
    }
}
