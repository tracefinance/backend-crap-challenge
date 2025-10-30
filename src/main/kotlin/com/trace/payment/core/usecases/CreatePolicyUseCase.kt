package com.trace.payment.core.usecases

import com.trace.payment.core.domain.entities.Policy
import com.trace.payment.core.domain.repositories.PolicyRepository
import com.trace.payment.presentation.requests.CreatePolicyRequest

class CreatePolicyUseCase(
    private val policyRepository: PolicyRepository
) {
    suspend fun execute(request: CreatePolicyRequest): Policy {
        // Problema: Validação massiva no UseCase
        if (request.name.isNullOrBlank()) {
            throw IllegalArgumentException("Policy name is required")
        }
        
        if (request.category.isNullOrBlank()) {
            throw IllegalArgumentException("Policy category is required")
        }
        
        if (request.category != "VALUE_LIMIT" && request.category != "TX_COUNT_LIMIT") {
            throw IllegalArgumentException("Invalid policy category")
        }
        
        if (request.maxPerPayment == null || request.maxPerPayment <= 0) {
            throw IllegalArgumentException("Max per payment must be positive")
        }
        
        if (request.daytimeDailyLimit == null || request.daytimeDailyLimit <= 0) {
            throw IllegalArgumentException("Daytime daily limit must be positive")
        }
        
        if (request.nighttimeDailyLimit == null || request.nighttimeDailyLimit <= 0) {
            throw IllegalArgumentException("Nighttime daily limit must be positive")
        }
        
        if (request.weekendDailyLimit == null || request.weekendDailyLimit <= 0) {
            throw IllegalArgumentException("Weekend daily limit must be positive")
        }
        
        val policy = Policy(
            name = request.name,
            category = request.category,
            maxPerPayment = request.maxPerPayment,
            daytimeDailyLimit = request.daytimeDailyLimit,
            nighttimeDailyLimit = request.nighttimeDailyLimit,
            weekendDailyLimit = request.weekendDailyLimit
        )
        
        return policyRepository.save(policy)
    }
}
