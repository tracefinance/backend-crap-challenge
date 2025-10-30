package com.trace.payment.core.usecases

import com.trace.payment.core.domain.entities.Payment
import com.trace.payment.core.domain.repositories.*
import com.trace.payment.presentation.requests.CreatePaymentRequest
import java.time.*

class ProcessPaymentUseCase(
    private val paymentRepository: PaymentRepository,
    private val walletRepository: WalletRepository,
    private val policyRepository: PolicyRepository
) {
    // Problema 16: UseCase gigante fazendo tudo
    suspend fun execute(walletId: String, request: CreatePaymentRequest): Payment {
        // Problema 17: Validações misturadas no UseCase
        if (request.amount == null || request.amount <= 0) {
            throw IllegalArgumentException("Amount must be positive")
        }
        
        if (request.amount > 1000.0) {
            throw IllegalArgumentException("Amount cannot exceed 1000")
        }
        
        if (request.occurredAt == null) {
            throw IllegalArgumentException("OccurredAt is required")
        }
        
        val occurredAt = try {
            Instant.parse(request.occurredAt)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid date format")
        }
        
        // Problema 18: UseCase fazendo múltiplas consultas
        val wallet = walletRepository.findById(walletId)
            ?: throw IllegalArgumentException("Wallet not found")
            
        val policy = policyRepository.findById(wallet.policyId)
            ?: throw IllegalStateException("Policy not found")
        
        // Problema 19: Lógica de negócio complexa no UseCase
        val zdt = occurredAt.atZone(ZoneId.systemDefault())
        val period = calculatePeriod(zdt) // Método privado dentro do UseCase
        val dailyLimit = getDailyLimit(policy, period)
        
        // Problema 20: Consulta ineficiente para calcular usage
        val today = zdt.toLocalDate().toString()
        val todayPayments = paymentRepository.findByWalletIdAndDateRange(
            walletId, 
            zdt.toLocalDate().atStartOfDay(zdt.zone).toInstant(),
            zdt.toLocalDate().atTime(23, 59, 59).atZone(zdt.zone).toInstant()
        )
        
        val currentUsage = todayPayments
            .filter { isSamePeriod(it.occurredAt, occurredAt) }
            .sumOf { it.amount }
        
        if (currentUsage + request.amount > dailyLimit) {
            throw IllegalStateException("Insufficient limit for period $period")
        }
        
        // Problema 21: UseCase criando entidade diretamente
        val payment = Payment(
            walletId = walletId,
            amount = request.amount,
            occurredAt = occurredAt
        )
        
        return paymentRepository.save(payment)
    }
    
    // Problema 22: Lógica de negócio privada no UseCase
    private fun calculatePeriod(zdt: ZonedDateTime): String {
        val hour = zdt.hour
        val dayOfWeek = zdt.dayOfWeek
        
        return when {
            dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY -> "weekend"
            hour >= 6 && hour < 18 -> "daytime"
            else -> "nighttime"
        }
    }
    
    private fun getDailyLimit(policy: com.trace.payment.core.domain.entities.Policy, period: String): Double {
        return when (period) {
            "weekend" -> policy.weekendDailyLimit
            "daytime" -> policy.daytimeDailyLimit
            "nighttime" -> policy.nighttimeDailyLimit
            else -> 0.0
        }
    }
    
    private fun isSamePeriod(paymentTime: Instant, requestTime: Instant): Boolean {
        val paymentZdt = paymentTime.atZone(ZoneId.systemDefault())
        val requestZdt = requestTime.atZone(ZoneId.systemDefault())
        return calculatePeriod(paymentZdt) == calculatePeriod(requestZdt)
    }
}
