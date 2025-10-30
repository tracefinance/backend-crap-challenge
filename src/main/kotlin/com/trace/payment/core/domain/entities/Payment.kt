package com.trace.payment.core.domain.entities

import java.time.Instant
import java.util.*

data class Payment(
    val id: String = UUID.randomUUID().toString(),
    val walletId: String,
    val amount: Double, // Problema 4: Double para dinheiro
    val occurredAt: Instant,
    val status: String = "APPROVED", // Problema 5: String ao invés de enum
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
