package com.trace.payment.core.domain.repositories

import com.trace.payment.core.domain.entities.Policy

interface PolicyRepository {
    suspend fun save(policy: Policy): Policy
    suspend fun findById(id: String): Policy?
    suspend fun findAll(): List<Policy>
}
