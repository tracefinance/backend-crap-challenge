package com.trace.payment

import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        jackson()
    }
    
    routing {
        get("/health") {
            call.respond(mapOf("status" to "OK"))
        }
        
        // TODO: Implementar rotas da API de pagamentos
        route("/wallets") {
            post {
                //TODO: Implementar endpoint
            }
            
            get("/{walletId}/limits") {
                //TODO: Implementar endpoint
            }
            
            put("/{walletId}/policy") {
                //TODO: Implementar endpoint
            }
            
            post("/{walletId}/payments") {
                //TODO: Implementar endpoint
            }
            
            get("/{walletId}/payments") {
                //TODO: Implementar endpoint
            }
        }
        
        route("/policies") {
            post {
                //TODO: Implementar endpoint
            }
            
            get {
                //TODO: Implementar endpoint
            }
        }
    }
}
