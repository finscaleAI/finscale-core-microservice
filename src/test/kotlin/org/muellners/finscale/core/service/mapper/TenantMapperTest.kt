package org.muellners.finscale.core.service.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TenantMapperTest {

    private lateinit var tenantMapper: TenantMapper

    @BeforeEach
    fun setUp() {
        tenantMapper = TenantMapperImpl()
    }

    @Test
    fun testEntityFromId() {
        val id = 1L
        assertThat(tenantMapper.fromId(id)?.id).isEqualTo(id)
        assertThat(tenantMapper.fromId(null)).isNull()
    }
}
