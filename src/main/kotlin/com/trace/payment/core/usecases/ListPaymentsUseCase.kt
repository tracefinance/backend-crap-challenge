package com.trace.payment.core.usecases

import com.trace.payment.core.domain.entities.Payment
import com.trace.payment.core.domain.repositories.PaymentRepository
import com.trace.payment.core.domain.repositories.WalletRepository
import java.time.Instant

class ListPaymentsUseCase(
    private val paymentRepository: PaymentRepository,
    private val walletRepository: WalletRepository
) {
    // Problema: UseCase fazendo validação e lógica de apresentação
    suspend fun execute(
        walletId: String, 
        startDate: String?, 
        endDate: String?, 
        cursor: String?
    ): Map<String, Any> {
        
        // Problema: Validação no UseCase
        val wallet = walletRepository.findById(walletId)
            ?: throw IllegalArgumentException("Wallet not found")
        
        // Problema: Parsing de data no UseCase
        val startInstant = startDate?.let { 
            try {
                Instant.parse(it)
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid start date format")
            }
        }
        
        val endInstant = endDate?.let { 
            try {
                Instant.parse(it)
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid end date format")
            }
        }
        
        val payments = paymentRepository.findByWalletIdAndDateRange(walletId, startInstant, endInstant)
        
        // Problema: UseCase montando response
        return mapOf(
            "data" to payments,
            "meta" to mapOf(
                "nextCursor" to null, // Problema: Paginação fake
                "previousCursor" to null,
                "total" to payments.size,
                "totalMatches" to null
            )
        )
    }
}
