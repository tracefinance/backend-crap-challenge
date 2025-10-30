package com.trace.payment.core.domain.entities

import java.time.Instant
import java.util.*

data class Payment(
    val id: String = UUID.randomUUID().toString(),
    val walletId: String,
    val amount: Double,
    val occurredAt: Instant,
    val status: String = "APPROVED",
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
