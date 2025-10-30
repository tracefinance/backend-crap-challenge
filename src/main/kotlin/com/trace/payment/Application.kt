package com.trace.payment

import com.trace.payment.infra.config.*
import com.trace.payment.infra.repositories.MongoPolicyRepository
import com.trace.payment.presentation.controllers.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.ktor.ext.inject

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        jackson {
            findAndRegisterModules()
        }
    }
    
    // Problema 1: Configuração do Koin ainda com problemas
    install(Koin) {
        slf4jLogger()
        modules(
            databaseModule,
            repositoryModule,
            useCaseModule,
            controllerModule
        )
    }
    
    // Problema 2: Inicialização síncrona no startup (bloqueia)
    runBlocking {
        val policyRepository by inject<MongoPolicyRepository>()
        policyRepository.ensureDefaultPolicy()
    }
    
    // Problema 3: Controllers injetados como propriedades (anti-pattern)
    val walletController by inject<WalletController>()
    val policyController by inject<PolicyController>()
    
    routing {
        get("/health") {
            call.respond(mapOf("status" to "OK"))
        }
        
        route("/wallets") {
            post { walletController.createWallet(call) }
            
            get("/{walletId}/policies") { walletController.getWalletPolicies(call) }
            
            put("/{walletId}/policy") { walletController.updateWalletPolicy(call) }
            
            post("/{walletId}/payments") { walletController.createPayment(call) }
            
            get("/{walletId}/payments") { walletController.listPayments(call) }
        }
        
        route("/policies") {
            post { policyController.createPolicy(call) }
            
            get { policyController.listPolicies(call) }
        }
    }
}
