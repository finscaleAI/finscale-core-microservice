package org.muellners.finscale.core.web.rest

import javax.persistence.EntityManager
import kotlin.test.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.muellners.finscale.core.CoreApp
import org.muellners.finscale.core.config.SecurityBeanOverrideConfiguration
import org.muellners.finscale.core.domain.Tenant
import org.muellners.finscale.core.repository.TenantRepository
import org.muellners.finscale.core.service.TenantService
import org.muellners.finscale.core.service.mapper.TenantMapper
import org.muellners.finscale.core.web.rest.errors.ExceptionTranslator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator

/**
 * Integration tests for the [TenantResource] REST controller.
 *
 * @see TenantResource
 */
@SpringBootTest(classes = [SecurityBeanOverrideConfiguration::class, CoreApp::class])
@AutoConfigureMockMvc
@WithMockUser
class TenantResourceIT {

    @Autowired
    private lateinit var tenantRepository: TenantRepository

    @Autowired
    private lateinit var tenantMapper: TenantMapper

    @Autowired
    private lateinit var tenantService: TenantService

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    private lateinit var restTenantMockMvc: MockMvc

    private lateinit var tenant: Tenant

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val tenantResource = TenantResource(tenantService)
         this.restTenantMockMvc = MockMvcBuilders.standaloneSetup(tenantResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        tenant = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createTenant() {
        val databaseSizeBeforeCreate = tenantRepository.findAll().size

        // Create the Tenant
        val tenantDTO = tenantMapper.toDto(tenant)
        restTenantMockMvc.perform(
            post("/api/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(tenantDTO))
        ).andExpect(status().isCreated)

        // Validate the Tenant in the database
        val tenantList = tenantRepository.findAll()
        assertThat(tenantList).hasSize(databaseSizeBeforeCreate + 1)
        val testTenant = tenantList[tenantList.size - 1]
        assertThat(testTenant.identifier).isEqualTo(DEFAULT_IDENTIFIER)
        assertThat(testTenant.name).isEqualTo(DEFAULT_NAME)
        assertThat(testTenant.description).isEqualTo(DEFAULT_DESCRIPTION)
    }

    @Test
    @Transactional
    fun createTenantWithExistingId() {
        val databaseSizeBeforeCreate = tenantRepository.findAll().size

        // Create the Tenant with an existing ID
        tenant.id = 1L
        val tenantDTO = tenantMapper.toDto(tenant)

        // An entity with an existing ID cannot be created, so this API call must fail
        restTenantMockMvc.perform(
            post("/api/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(tenantDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Tenant in the database
        val tenantList = tenantRepository.findAll()
        assertThat(tenantList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkIdentifierIsRequired() {
        val databaseSizeBeforeTest = tenantRepository.findAll().size
        // set the field null
        tenant.identifier = null

        // Create the Tenant, which fails.
        val tenantDTO = tenantMapper.toDto(tenant)

        restTenantMockMvc.perform(
            post("/api/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(tenantDTO))
        ).andExpect(status().isBadRequest)

        val tenantList = tenantRepository.findAll()
        assertThat(tenantList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = tenantRepository.findAll().size
        // set the field null
        tenant.name = null

        // Create the Tenant, which fails.
        val tenantDTO = tenantMapper.toDto(tenant)

        restTenantMockMvc.perform(
            post("/api/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(tenantDTO))
        ).andExpect(status().isBadRequest)

        val tenantList = tenantRepository.findAll()
        assertThat(tenantList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllTenants() {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant)

        // Get all the tenantList
        restTenantMockMvc.perform(get("/api/tenants?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tenant.id?.toInt())))
            .andExpect(jsonPath("$.[*].identifier").value(hasItem(DEFAULT_IDENTIFIER)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getTenant() {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant)

        val id = tenant.id
        assertNotNull(id)

        // Get the tenant
        restTenantMockMvc.perform(get("/api/tenants/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tenant.id?.toInt()))
            .andExpect(jsonPath("$.identifier").value(DEFAULT_IDENTIFIER))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION)) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingTenant() {
        // Get the tenant
        restTenantMockMvc.perform(get("/api/tenants/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateTenant() {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant)

        val databaseSizeBeforeUpdate = tenantRepository.findAll().size

        // Update the tenant
        val id = tenant.id
        assertNotNull(id)
        val updatedTenant = tenantRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedTenant are not directly saved in db
        em.detach(updatedTenant)
        updatedTenant.identifier = UPDATED_IDENTIFIER
        updatedTenant.name = UPDATED_NAME
        updatedTenant.description = UPDATED_DESCRIPTION
        val tenantDTO = tenantMapper.toDto(updatedTenant)

        restTenantMockMvc.perform(
            put("/api/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(tenantDTO))
        ).andExpect(status().isOk)

        // Validate the Tenant in the database
        val tenantList = tenantRepository.findAll()
        assertThat(tenantList).hasSize(databaseSizeBeforeUpdate)
        val testTenant = tenantList[tenantList.size - 1]
        assertThat(testTenant.identifier).isEqualTo(UPDATED_IDENTIFIER)
        assertThat(testTenant.name).isEqualTo(UPDATED_NAME)
        assertThat(testTenant.description).isEqualTo(UPDATED_DESCRIPTION)
    }

    @Test
    @Transactional
    fun updateNonExistingTenant() {
        val databaseSizeBeforeUpdate = tenantRepository.findAll().size

        // Create the Tenant
        val tenantDTO = tenantMapper.toDto(tenant)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTenantMockMvc.perform(
            put("/api/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(tenantDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Tenant in the database
        val tenantList = tenantRepository.findAll()
        assertThat(tenantList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteTenant() {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant)

        val databaseSizeBeforeDelete = tenantRepository.findAll().size

        // Delete the tenant
        restTenantMockMvc.perform(
            delete("/api/tenants/{id}", tenant.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val tenantList = tenantRepository.findAll()
        assertThat(tenantList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_IDENTIFIER = "AAAAAAAAAA"
        private const val UPDATED_IDENTIFIER = "BBBBBBBBBB"

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_DESCRIPTION = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Tenant {
            val tenant = Tenant(
                identifier = DEFAULT_IDENTIFIER,
                name = DEFAULT_NAME,
                description = DEFAULT_DESCRIPTION
            )

            return tenant
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Tenant {
            val tenant = Tenant(
                identifier = UPDATED_IDENTIFIER,
                name = UPDATED_NAME,
                description = UPDATED_DESCRIPTION
            )

            return tenant
        }
    }
}
