package com.trace.payment

import com.fasterxml.jackson.databind.ObjectMapper
import com.trace.payment.infra.config.*
import com.trace.payment.presentation.controllers.*
import io.ktor.http.ContentType
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.context.GlobalContext.startKoin
import org.koin.ktor.ext.inject

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    val modules = listOf(
        databaseModule,
        controllerModule,
        sharedModule
    )
    startKoin { modules(modules) }

    val mapper by inject<ObjectMapper>()
    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(mapper))
    }

    val walletController by inject<WalletController>()
    
    routing {
        get("/health") {
            call.respond(mapOf("status" to "OK"))
        }
        
        route("/wallets") {
            post { walletController.createWallet(call) }
                    
            post("/{walletId}/payments") { walletController.createPayment(call) }
        }
    }
}
