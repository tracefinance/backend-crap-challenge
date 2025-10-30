package com.trace.payment.presentation.requests

data class CreatePaymentRequest(
    val amount: Double?,
    val occurredAt: String?
)
