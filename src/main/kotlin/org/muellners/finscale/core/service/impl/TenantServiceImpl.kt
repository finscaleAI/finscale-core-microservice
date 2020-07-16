package org.muellners.finscale.core.service.impl

import java.util.Optional
import org.muellners.finscale.core.domain.Tenant
import org.muellners.finscale.core.repository.TenantRepository
import org.muellners.finscale.core.service.TenantService
import org.muellners.finscale.core.service.mapper.TenantMapper
import org.muellners.finscale.multitenancy.service.dto.TenantDTO
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.connection.stream.StreamRecords
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [Tenant].
 */
@Service
@Transactional
class TenantServiceImpl(
    private val tenantRepository: TenantRepository,
    private val tenantMapper: TenantMapper,
    private val redisTemplate: StringRedisTemplate
//    private val redisTemplate: RedisTemplate<String, Tenant>
) : TenantService {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a tenant.
     *
     * @param tenantDTO the entity to save.
     * @return the persisted entity.
     */
    override fun save(tenantDTO: TenantDTO): TenantDTO {
        log.debug("Request to save Tenant : {}", tenantDTO)

        var tenant = tenantMapper.toEntity(tenantDTO)
        tenant = tenantRepository.save(tenant)
        val tenantDTO = tenantMapper.toDto(tenant)
        val tenantRecord: ObjectRecord<String, TenantDTO> = StreamRecords.newRecord()
            .ofObject(tenantDTO).withStreamKey("core:tenants")
        this.redisTemplate.opsForStream<String, TenantDTO>().add(tenantRecord)

        return tenantDTO
    }

//    override fun save(tenantDTO: TenantDTO): TenantDTO {
//        log.debug("Request to save Tenant : {}", tenantDTO)
//
//        var tenant = tenantMapper.toEntity(tenantDTO)
//        tenant = tenantRepository.save(tenant)
//        val tenantRecord: ObjectRecord<String, Tenant> = StreamRecords.newRecord()
//            .ofObject(tenant).withStreamKey("core:tenants")
//        this.redisTemplate.opsForStream<String, Tenant>().add(tenantRecord)
//
//        return tenantMapper.toDto(tenant)
//    }

    /**
     * Get all the tenants.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun findAll(): MutableList<TenantDTO> {
        log.debug("Request to get all Tenants")
        return tenantRepository.findAll()
            .mapTo(mutableListOf(), tenantMapper::toDto)
    }

    /**
     * Get one tenant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<TenantDTO> {
        log.debug("Request to get Tenant : {}", id)
        return tenantRepository.findById(id)
            .map(tenantMapper::toDto)
    }

    /**
     * Delete the tenant by id.
     *
     * @param id the id of the entity.
     */
    override fun delete(id: Long) {
        log.debug("Request to delete Tenant : {}", id)

        tenantRepository.deleteById(id)
    }
}
