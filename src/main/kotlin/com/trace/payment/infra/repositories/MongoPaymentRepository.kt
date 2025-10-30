package com.trace.payment.infra.repositories

import com.mongodb.reactivestreams.client.MongoDatabase
import com.trace.payment.core.domain.entities.Payment
import com.trace.payment.core.domain.repositories.PaymentRepository
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.gte
import org.litote.kmongo.lte
import org.litote.kmongo.and
import java.time.Instant

class MongoPaymentRepository(
    private val database: MongoDatabase
) : PaymentRepository {
    
    private val collection = database.getCollection("payments", Payment::class.java).coroutine
    
    override suspend fun save(payment: Payment): Payment {
        // Problema 1: Não verifica duplicatas/idempotência
        collection.insertOne(payment)
        return payment
    }
    
    override suspend fun findByWalletId(walletId: String): List<Payment> {
        // Problema 2: Query sem limit - pode retornar milhões de registros
        return collection.find(Payment::walletId eq walletId).toList()
    }
    
    override suspend fun findByWalletIdAndDateRange(
        walletId: String, 
        startDate: Instant?, 
        endDate: Instant?
    ): List<Payment> {
        // Problema 3: Query complexa sem índices compostos
        val filters = mutableListOf<org.bson.conversions.Bson>()
        filters.add(Payment::walletId eq walletId)
        
        startDate?.let { filters.add(Payment::occurredAt gte it) }
        endDate?.let { filters.add(Payment::occurredAt lte it) }
        
        // Problema 4: Sem paginação
        return collection.find(and(filters)).toList()
    }
    
    override suspend fun countByWalletIdAndDate(walletId: String, date: String): Int {
        // Problema 5: Comparação de data como string (ineficiente)
        val payments = collection.find(Payment::walletId eq walletId).toList()
        return payments.count { it.occurredAt.toString().startsWith(date) }
    }
    
    override suspend fun findByWalletIdAndPeriod(
        walletId: String, 
        startDate: Instant, 
        endDate: Instant
    ): List<Payment> {
        // Problema 6: Código duplicado com findByWalletIdAndDateRange
        return collection.find(
            and(
                Payment::walletId eq walletId,
                Payment::occurredAt gte startDate,
                Payment::occurredAt lte endDate
            )
        ).toList()
    }
}
