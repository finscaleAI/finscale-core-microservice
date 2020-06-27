package org.muellners.finscale.core.repository

import org.muellners.finscale.core.domain.Tenant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Tenant] entity.
 */
@Suppress("unused")
@Repository
interface TenantRepository : JpaRepository<Tenant, Long>
