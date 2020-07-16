package org.muellners.finscale.core.service.mapper

import org.mapstruct.Mapper
import org.muellners.finscale.core.domain.Tenant
import org.muellners.finscale.multitenancy.service.dto.TenantDTO

/**
 * Mapper for the entity [Tenant] and its DTO [TenantDTO].
 */
@Mapper(componentModel = "spring", uses = [])
interface TenantMapper :
    EntityMapper<TenantDTO, Tenant> {

    override fun toEntity(tenantDTO: TenantDTO): Tenant

    @JvmDefault
    fun fromId(id: Long?) = id?.let {
        val tenant = Tenant()
        tenant.id = id
        tenant
    }
}
