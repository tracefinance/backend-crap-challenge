package com.trace.payment.infra.config

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.trace.payment.presentation.controllers.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import org.koin.dsl.module

val controllerModule = module {
    single { WalletController(get(), get()) }
}

val databaseModule = module {
    single {
        val mongoUri = System.getenv("MONGO_URI") ?: "mongodb://root:root@localhost:27017/payment_api"
        com.mongodb.reactivestreams.client.MongoClients.create(mongoUri)
    }
    single {
        get<com.mongodb.reactivestreams.client.MongoClient>().getDatabase("payment_api") 
    }
}

val sharedModule = module {
    single { jacksonObjectMapper().apply {
        setDefaultPrettyPrinter(
            DefaultPrettyPrinter().apply {
                indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                indentObjectsWith(DefaultIndenter("  ", "\n"))
            }
        )

        val javaTimeModule = JavaTimeModule().apply {
            val customZonedDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSXX")

            addSerializer(ZonedDateTime::class.java, ZonedDateTimeSerializer(customZonedDateTimeFormatter))
        }

        registerModule(javaTimeModule)

        enable(SerializationFeature.INDENT_OUTPUT)
        enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)

        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    } }
}
