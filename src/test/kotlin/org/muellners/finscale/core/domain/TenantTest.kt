package org.muellners.finscale.core.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.muellners.finscale.core.web.rest.equalsVerifier

class TenantTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Tenant::class)
        val tenant1 = Tenant()
        tenant1.id = 1L
        val tenant2 = Tenant()
        tenant2.id = tenant1.id
        assertThat(tenant1).isEqualTo(tenant2)
        tenant2.id = 2L
        assertThat(tenant1).isNotEqualTo(tenant2)
        tenant1.id = null
        assertThat(tenant1).isNotEqualTo(tenant2)
    }
}
