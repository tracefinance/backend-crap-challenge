package com.trace.payment.core.domain.entities

import java.time.Instant
import java.util.*

data class Policy(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String, // Problema 6: String ao invés de enum
    val maxPerPayment: Double,
    val daytimeDailyLimit: Double,
    val nighttimeDailyLimit: Double,
    val weekendDailyLimit: Double,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
