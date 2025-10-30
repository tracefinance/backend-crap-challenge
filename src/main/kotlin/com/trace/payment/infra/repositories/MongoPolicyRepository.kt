package com.trace.payment.infra.repositories

import com.mongodb.reactivestreams.client.MongoDatabase
import com.trace.payment.core.domain.entities.Policy
import com.trace.payment.core.domain.repositories.PolicyRepository
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq

class MongoPolicyRepository(
    private val database: MongoDatabase
) : PolicyRepository {
    
    private val collection = database.getCollection("policies", Policy::class.java).coroutine
    
    // Problema 1: Inicialização no repositório (deveria ser em migration)
    suspend fun ensureDefaultPolicy() {
        val existingDefault = collection.findOne(Policy::id eq "default-policy-id")
        if (existingDefault == null) {
            val defaultPolicy = Policy(
                id = "default-policy-id",
                name = "DEFAULT_VALUE_LIMIT",
                category = "VALUE_LIMIT",
                maxPerPayment = 1000.0,
                daytimeDailyLimit = 4000.0,
                nighttimeDailyLimit = 1000.0,
                weekendDailyLimit = 1000.0
            )
            collection.insertOne(defaultPolicy)
        }
    }
    
    override suspend fun save(policy: Policy): Policy {
        // Problema 2: Sempre insert, nunca update
        collection.insertOne(policy)
        return policy
    }
    
    override suspend fun findById(id: String): Policy? {
        return collection.findOne(Policy::id eq id)
    }
    
    override suspend fun findAll(): List<Policy> {
        // Problema 3: Sem limit ou paginação
        return collection.find().toList()
    }
}
