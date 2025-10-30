package com.trace.payment.infra.config

import com.trace.payment.core.domain.repositories.*
import com.trace.payment.core.usecases.*
import com.trace.payment.infra.repositories.*
import com.trace.payment.presentation.controllers.*
import org.koin.dsl.module

// Problema: Configuração de DI ainda com problemas intencionais
val repositoryModule = module {
    // Problema 1: Singleton para repositórios que deveriam ser scoped
    single<WalletRepository> { MongoWalletRepository(get()) }
    single<PaymentRepository> { MongoPaymentRepository(get()) }
    single<PolicyRepository> { MongoPolicyRepository(get()) }
}

val useCaseModule = module {
    // Problema 2: Use Cases como singleton (podem ter estado)
    single { CreateWalletUseCase(get()) }
    single { ProcessPaymentUseCase(get(), get(), get()) }
    single { ListPaymentsUseCase(get(), get()) }
    single { GetWalletPoliciesUseCase(get(), get()) }
    single { UpdateWalletPolicyUseCase(get(), get()) }
    single { CreatePolicyUseCase(get()) }
    single { ListPoliciesUseCase(get()) }
}

val controllerModule = module {
    // Problema 3: Controllers como singleton
    single { WalletController(get(), get(), get(), get(), get()) }
    single { PolicyController(get(), get()) }
}

// Problema 4: Configuração do MongoDB com problemas
val databaseModule = module {
    single { 
        // Problema 5: Fallback para localhost sem validação
        val mongoUri = System.getenv("MONGO_URI") ?: "mongodb://localhost:27017/payment_api"
        com.mongodb.reactivestreams.client.MongoClients.create(mongoUri)
    }
    single { 
        // Problema 6: Nome do banco hardcoded
        get<com.mongodb.reactivestreams.client.MongoClient>().getDatabase("payment_api") 
    }
}
