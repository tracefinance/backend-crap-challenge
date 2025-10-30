package com.trace.payment.presentation.controllers

import com.trace.payment.core.usecases.CreatePolicyUseCase
import com.trace.payment.core.usecases.ListPoliciesUseCase
import com.trace.payment.presentation.requests.CreatePolicyRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class PolicyController(
    private val createPolicyUseCase: CreatePolicyUseCase,
    private val listPoliciesUseCase: ListPoliciesUseCase
) {
    suspend fun createPolicy(call: ApplicationCall) {
        try {
            val request = call.receive<CreatePolicyRequest>()
            val policy = createPolicyUseCase.execute(request)
            call.respond(HttpStatusCode.Created, policy)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal error"))
        }
    }
    
    suspend fun listPolicies(call: ApplicationCall) {
        try {
            val result = listPoliciesUseCase.execute()
            call.respond(HttpStatusCode.OK, result)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal error"))
        }
    }
}
