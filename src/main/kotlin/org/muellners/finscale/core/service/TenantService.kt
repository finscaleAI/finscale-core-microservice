package org.muellners.finscale.core.service
import java.util.Optional
import org.muellners.finscale.multitenancy.service.dto.TenantDTO

/**
 * Service Interface for managing [org.muellners.finscale.core.domain.Tenant].
 */
interface TenantService {

    /**
     * Save a tenant.
     *
     * @param tenantDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(tenantDTO: TenantDTO): TenantDTO

    /**
     * Get all the tenants.
     *
     * @return the list of entities.
     */
    fun findAll(): MutableList<TenantDTO>

    /**
     * Get the "id" tenant.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<TenantDTO>

    /**
     * Delete the "id" tenant.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)
}
