package com.trace.payment.presentation.requests

// Problema 23: Request sem validação
data class CreateWalletRequest(
    val ownerName: String? // Problema 24: Nullable quando não deveria ser
)
