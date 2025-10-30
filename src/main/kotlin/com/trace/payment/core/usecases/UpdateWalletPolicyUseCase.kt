package com.trace.payment.core.usecases

import com.trace.payment.core.domain.repositories.WalletRepository
import com.trace.payment.core.domain.repositories.PolicyRepository
import com.trace.payment.presentation.requests.UpdateWalletPolicyRequest

class UpdateWalletPolicyUseCase(
    private val walletRepository: WalletRepository,
    private val policyRepository: PolicyRepository
) {
    suspend fun execute(walletId: String, request: UpdateWalletPolicyRequest): Boolean {
        // Problema: Validação no UseCase
        if (request.policyId.isNullOrBlank()) {
            throw IllegalArgumentException("Policy ID is required")
        }
        
        val wallet = walletRepository.findById(walletId)
            ?: throw IllegalArgumentException("Wallet not found")
        
        val policy = policyRepository.findById(request.policyId)
            ?: throw IllegalArgumentException("Policy not found")
        
        return walletRepository.updatePolicy(walletId, request.policyId)
    }
}
