package com.trace.payment.core.usecases

import com.trace.payment.core.domain.repositories.PolicyRepository

class ListPoliciesUseCase(
    private val policyRepository: PolicyRepository
) {
    suspend fun execute(): Map<String, Any> {
        val policies = policyRepository.findAll()
        
        // Problema: UseCase montando response
        return mapOf(
            "data" to policies,
            "meta" to mapOf(
                "nextCursor" to null,
                "previousCursor" to null,
                "total" to policies.size,
                "totalMatches" to null
            )
        )
    }
}
