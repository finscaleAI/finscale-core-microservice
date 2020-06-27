package org.muellners.finscale.core.config

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.zalando.problem.ProblemModule
import org.zalando.problem.violations.ConstraintViolationProblemModule

@Configuration
class JacksonConfiguration {

    /**
     * Support for Java date and time API.
     * @return the corresponding Jackson module.
     */
    @Bean
    fun javaTimeModule() = JavaTimeModule()

    @Bean
    fun jdk8TimeModule() = Jdk8Module()

    /*
     * Support for Hibernate types in Jackson.
     */
    @Bean
    fun hibernate5Module() = Hibernate5Module()

    /*
     * Module for serialization/deserialization of RFC7807 Problem.
     */
    @Bean
    fun problemModule() = ProblemModule()

    /*
     * Module for serialization/deserialization of ConstraintViolationProblem.
     */
    @Bean
    fun constraintViolationProblemModule() = ConstraintViolationProblemModule()
}
