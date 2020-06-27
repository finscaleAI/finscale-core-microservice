package org.muellners.finscale.core.service.dto

import java.io.Serializable
import javax.validation.constraints.NotNull

/**
 * A DTO for the [org.muellners.finscale.core.domain.Tenant] entity.
 */
data class TenantDTO(

    var id: Long? = null,

    @get: NotNull
    var identifier: String? = null,

    @get: NotNull
    var name: String? = null,

    var description: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TenantDTO) return false
        return id != null && id == other.id
    }

    override fun hashCode() = 31
}
