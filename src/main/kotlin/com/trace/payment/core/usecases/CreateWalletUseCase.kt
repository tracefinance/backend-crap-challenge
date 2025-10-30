package com.trace.payment.core.usecases

import com.trace.payment.core.domain.entities.Wallet
import com.trace.payment.core.domain.repositories.WalletRepository
import com.trace.payment.presentation.requests.CreateWalletRequest // Problema 10: UseCase conhecendo Request

class CreateWalletUseCase(
    private val walletRepository: WalletRepository
) {
    // Problema 11: UseCase recebendo Request ao invés de Command
    suspend fun execute(request: CreateWalletRequest): Wallet {
        // Problema 12: Validação no UseCase ao invés de na Request
        if (request.ownerName.isNullOrBlank()) {
            throw IllegalArgumentException("Owner name cannot be empty")
        }
        
        if (request.ownerName.length < 2) {
            throw IllegalArgumentException("Owner name must have at least 2 characters")
        }
        
        if (request.ownerName.length > 100) {
            throw IllegalArgumentException("Owner name cannot exceed 100 characters")
        }
        
        // Problema 13: UseCase fazendo múltiplas responsabilidades
        val existingWallets = walletRepository.findByOwnerName(request.ownerName)
        if (existingWallets.size >= 5) { // Problema 14: Magic number
            throw IllegalStateException("User cannot have more than 5 wallets")
        }
        
        val wallet = Wallet(
            ownerName = request.ownerName.trim().uppercase() // Problema 15: Transformação no UseCase
        )
        
        return walletRepository.save(wallet)
    }
}
