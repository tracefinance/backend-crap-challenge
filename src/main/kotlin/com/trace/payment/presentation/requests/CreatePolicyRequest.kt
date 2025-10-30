package com.trace.payment.presentation.requests

data class CreatePolicyRequest(
    val name: String?,
    val category: String?,
    val maxPerPayment: Double?,
    val daytimeDailyLimit: Double?,
    val nighttimeDailyLimit: Double?,
    val weekendDailyLimit: Double?
)
