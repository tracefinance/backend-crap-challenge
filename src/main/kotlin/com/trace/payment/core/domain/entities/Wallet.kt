package com.trace.payment.core.domain.entities

import java.time.Instant
import java.util.*

// Problema 1: Domain anêmico - só dados, sem comportamento
data class Wallet(
    val id: String = UUID.randomUUID().toString(),
    val ownerName: String,
    val policyId: String = "default-policy-id", // Problema 2: Hardcoded default
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
) {
    // Problema 3: Entidade sem métodos de negócio
    // Deveria ter métodos como canMakePayment(), getCurrentUsage(), etc.
}
