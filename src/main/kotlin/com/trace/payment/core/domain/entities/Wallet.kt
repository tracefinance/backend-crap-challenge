package com.trace.payment.core.domain.entities

import java.time.Instant
import java.util.*

data class Wallet(
    val id: String = UUID.randomUUID().toString(),
    val ownerName: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
