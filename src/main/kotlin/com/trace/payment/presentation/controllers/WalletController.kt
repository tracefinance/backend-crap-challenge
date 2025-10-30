package com.trace.payment.presentation.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.reactivestreams.client.MongoDatabase
import com.trace.payment.core.domain.entities.Payment
import com.trace.payment.core.domain.entities.Wallet
import com.trace.payment.presentation.requests.CreatePaymentRequest
import com.trace.payment.presentation.requests.CreateWalletRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import java.time.Instant
import org.bson.Document
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq

class WalletController(
    private val database: MongoDatabase,
    private val objectMapper: ObjectMapper,
) {
    private val collectionWallet = database.getCollection("wallets").coroutine
    private val collectionPayment = database.getCollection("payments").coroutine

    suspend fun createWallet(call: ApplicationCall) {
        try {
            val request = call.receive<CreateWalletRequest>()

            if (request.ownerName.isNullOrBlank()) {
                throw IllegalArgumentException("Owner name cannot be empty")
            }

            if (request.ownerName.length < 2) {
                throw IllegalArgumentException("Owner name must have at least 2 characters")
            }

            val existingWallets = collectionWallet.find(Wallet::ownerName eq request.ownerName).toList()

            if (existingWallets.isEmpty()) {
                throw IllegalStateException("User already exists")
            }

            val wallet = Wallet(
                ownerName = request.ownerName.trim().uppercase()
            )
            collectionWallet.save(Document.parse(objectMapper.writeValueAsString(wallet)))

            call.respond(HttpStatusCode.Created, wallet)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal error"))
        }
    }
    
    suspend fun createPayment(call: ApplicationCall) {
        try {
            val walletId = call.parameters["walletId"] ?: throw IllegalArgumentException("Wallet ID required")
            val request = call.receive<CreatePaymentRequest>()

            val occurredAt = try {
                Instant.parse(request.occurredAt!!)
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid date format")
            }

            val wallet = collectionWallet.findOne(Wallet::id eq walletId)
                ?: throw IllegalArgumentException("Wallet not found")

            // Implement limit policies

            val payment = Payment(
                walletId = walletId,
                amount = request.amount!!,
                occurredAt = occurredAt
            )

            collectionPayment.insertOne(Document.parse(objectMapper.writeValueAsString(payment)))

            val response = mapOf(
                "paymentId" to payment.id,
                "status" to payment.status,
                "amount" to payment.amount,
                "occurredAt" to payment.occurredAt.toString()
            )
            
            call.respond(HttpStatusCode.Created, response)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        } catch (e: IllegalStateException) {
            call.respond(HttpStatusCode.UnprocessableEntity, mapOf("error" to e.message))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal error"))
        }
    }
}
