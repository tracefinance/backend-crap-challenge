package com.trace.payment.core.domain.repositories

import com.trace.payment.core.domain.entities.Payment
import java.time.Instant

interface PaymentRepository {
    suspend fun save(payment: Payment): Payment
    suspend fun findByWalletId(walletId: String): List<Payment>
    suspend fun findByWalletIdAndDateRange(
        walletId: String, 
        startDate: Instant?, 
        endDate: Instant?
    ): List<Payment>
    suspend fun countByWalletIdAndDate(walletId: String, date: String): Int // Problema 9: String para data
    suspend fun findByWalletIdAndPeriod(walletId: String, startDate: Instant, endDate: Instant): List<Payment>
}
