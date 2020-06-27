package org.muellners.finscale.core.service.dto

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.muellners.finscale.core.web.rest.equalsVerifier

class TenantDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(TenantDTO::class)
        val tenantDTO1 = TenantDTO()
        tenantDTO1.id = 1L
        val tenantDTO2 = TenantDTO()
        assertThat(tenantDTO1).isNotEqualTo(tenantDTO2)
        tenantDTO2.id = tenantDTO1.id
        assertThat(tenantDTO1).isEqualTo(tenantDTO2)
        tenantDTO2.id = 2L
        assertThat(tenantDTO1).isNotEqualTo(tenantDTO2)
        tenantDTO1.id = null
        assertThat(tenantDTO1).isNotEqualTo(tenantDTO2)
    }
}
