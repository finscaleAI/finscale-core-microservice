package org.muellners.finscale.core.web.rest

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid
import org.muellners.finscale.core.service.TenantService
import org.muellners.finscale.core.web.rest.errors.BadRequestAlertException
import org.muellners.finscale.multitenancy.service.dto.TenantDTO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private const val ENTITY_NAME = "coreTenant"
/**
 * REST controller for managing [org.muellners.finscale.core.domain.Tenant].
 */
@RestController
@RequestMapping("/api")
class TenantResource(
    private val tenantService: TenantService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /tenants` : Create a new tenant.
     *
     * @param tenantDTO the tenantDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new tenantDTO, or with status `400 (Bad Request)` if the tenant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tenants")
    fun createTenant(@Valid @RequestBody tenantDTO: TenantDTO): ResponseEntity<TenantDTO> {
        log.debug("REST request to save Tenant : {}", tenantDTO)
        if (tenantDTO.id != null) {
            throw BadRequestAlertException(
                "A new tenant cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = tenantService.save(tenantDTO)
        return ResponseEntity.created(URI("/api/tenants/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /tenants` : Updates an existing tenant.
     *
     * @param tenantDTO the tenantDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated tenantDTO,
     * or with status `400 (Bad Request)` if the tenantDTO is not valid,
     * or with status `500 (Internal Server Error)` if the tenantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tenants")
    fun updateTenant(@Valid @RequestBody tenantDTO: TenantDTO): ResponseEntity<TenantDTO> {
        log.debug("REST request to update Tenant : {}", tenantDTO)
        if (tenantDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = tenantService.save(tenantDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     tenantDTO.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /tenants` : get all the tenants.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of tenants in body.
     */
    @GetMapping("/tenants")
    fun getAllTenants(): MutableList<TenantDTO> {
        log.debug("REST request to get all Tenants")

        return tenantService.findAll()
            }

    /**
     * `GET  /tenants/:id` : get the "id" tenant.
     *
     * @param id the id of the tenantDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the tenantDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/tenants/{id}")
    fun getTenant(@PathVariable id: Long): ResponseEntity<TenantDTO> {
        log.debug("REST request to get Tenant : {}", id)
        val tenantDTO = tenantService.findOne(id)
        return ResponseUtil.wrapOrNotFound(tenantDTO)
    }
    /**
     *  `DELETE  /tenants/:id` : delete the "id" tenant.
     *
     * @param id the id of the tenantDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/tenants/{id}")
    fun deleteTenant(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Tenant : {}", id)

        tenantService.delete(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
