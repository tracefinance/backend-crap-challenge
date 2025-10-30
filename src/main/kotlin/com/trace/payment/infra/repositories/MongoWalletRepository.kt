package com.trace.payment.infra.repositories

import com.mongodb.reactivestreams.client.MongoDatabase
import com.trace.payment.core.domain.entities.Wallet
import com.trace.payment.core.domain.repositories.WalletRepository
import org.bson.Document
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class MongoWalletRepository(
    private val database: MongoDatabase
) : WalletRepository {
    
    // Problema 1: Nome da coleção hardcoded
    private val collection = database.getCollection("wallets", Wallet::class.java).coroutine
    
    override suspend fun save(wallet: Wallet): Wallet {
        // Problema 2: Sempre faz upsert, não diferencia insert de update
        collection.save(wallet)
        return wallet
    }
    
    override suspend fun findById(id: String): Wallet? {
        return collection.findOne(Wallet::id eq id)
    }
    
    override suspend fun findByOwnerName(ownerName: String): List<Wallet> {
        // Problema 3: Query case-sensitive e sem índice otimizado
        return collection.find(Wallet::ownerName eq ownerName).toList()
    }
    
    override suspend fun findAllAsDocuments(): List<Document> {
        // Problema 4: Vazamento de implementação - retorna Document do MongoDB
        val documentCollection = database.getCollection("wallets").coroutine
        return documentCollection.find().toList()
    }
    
    override suspend fun updatePolicy(walletId: String, policyId: String): Boolean {
        // Problema 5: Update sem verificar se o documento existe
        val result = collection.updateOne(
            Wallet::id eq walletId,
            setValue(Wallet::policyId, policyId)
        )
        // Problema 6: Não verifica se realmente atualizou
        return result.modifiedCount > 0
    }
}
