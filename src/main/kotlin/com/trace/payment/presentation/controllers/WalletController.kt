package com.trace.payment.presentation.controllers

import com.trace.payment.core.usecases.*
import com.trace.payment.presentation.requests.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class WalletController(
    private val createWalletUseCase: CreateWalletUseCase,
    private val processPaymentUseCase: ProcessPaymentUseCase,
    private val listPaymentsUseCase: ListPaymentsUseCase,
    private val getPoliciesUseCase: GetWalletPoliciesUseCase,
    private val updatePolicyUseCase: UpdateWalletPolicyUseCase
) {
    suspend fun createWallet(call: ApplicationCall) {
        try {
            val request = call.receive<CreateWalletRequest>()
            // Problema 25: Controller passando Request diretamente
            val wallet = createWalletUseCase.execute(request)
            call.respond(HttpStatusCode.Created, wallet)
        } catch (e: IllegalArgumentException) {
            // Problema 26: Exception handling genérico
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal error"))
        }
    }
    
    suspend fun createPayment(call: ApplicationCall) {
        try {
            val walletId = call.parameters["walletId"] ?: throw IllegalArgumentException("Wallet ID required")
            val request = call.receive<CreatePaymentRequest>()
            
            val payment = processPaymentUseCase.execute(walletId, request)
            
            // Problema 27: Response mal estruturada
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
    
    suspend fun listPayments(call: ApplicationCall) {
        try {
            val walletId = call.parameters["walletId"] ?: throw IllegalArgumentException("Wallet ID required")
            val startDate = call.request.queryParameters["startDate"]
            val endDate = call.request.queryParameters["endDate"]
            val cursor = call.request.queryParameters["cursor"]
            
            // Problema: Controller passando parâmetros diretos
            val result = listPaymentsUseCase.execute(walletId, startDate, endDate, cursor)
            call.respond(HttpStatusCode.OK, result)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal error"))
        }
    }
    
    suspend fun getWalletPolicies(call: ApplicationCall) {
        try {
            val walletId = call.parameters["walletId"] ?: throw IllegalArgumentException("Wallet ID required")
            val result = getPoliciesUseCase.execute(walletId)
            call.respond(HttpStatusCode.OK, result)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal error"))
        }
    }
    
    suspend fun updateWalletPolicy(call: ApplicationCall) {
        try {
            val walletId = call.parameters["walletId"] ?: throw IllegalArgumentException("Wallet ID required")
            val request = call.receive<UpdateWalletPolicyRequest>()
            
            updatePolicyUseCase.execute(walletId, request)
            call.respond(HttpStatusCode.OK, mapOf("message" to "Policy updated"))
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal error"))
        }
    }
}
