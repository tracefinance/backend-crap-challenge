package com.trace.payment.core.domain.repositories

import com.trace.payment.core.domain.entities.Wallet
import org.bson.Document // Problema 7: Domain conhecendo MongoDB

interface WalletRepository {
    suspend fun save(wallet: Wallet): Wallet
    suspend fun findById(id: String): Wallet?
    suspend fun findByOwnerName(ownerName: String): List<Wallet>
    suspend fun findAllAsDocuments(): List<Document> // Problema 8: Vazando implementação
    suspend fun updatePolicy(walletId: String, policyId: String): Boolean
}
