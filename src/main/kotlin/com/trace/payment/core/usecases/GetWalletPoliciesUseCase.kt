package com.trace.payment.core.usecases

import com.trace.payment.core.domain.repositories.WalletRepository
import com.trace.payment.core.domain.repositories.PolicyRepository

class GetWalletPoliciesUseCase(
    private val walletRepository: WalletRepository,
    private val policyRepository: PolicyRepository
) {
    suspend fun execute(walletId: String): Map<String, Any> {
        val wallet = walletRepository.findById(walletId)
            ?: throw IllegalArgumentException("Wallet not found")
        
        val policy = policyRepository.findById(wallet.policyId)
            ?: throw IllegalStateException("Policy not found")
        
        // Problema: UseCase montando response
        return mapOf(
            "data" to listOf(policy),
            "meta" to mapOf("total" to 1)
        )
    }
}
