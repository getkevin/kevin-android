package eu.kevin.sample.networking.api

import eu.kevin.sample.networking.entities.authorization.AuthStateRequest
import eu.kevin.sample.networking.entities.payments.InitiatePaymentRequest
import eu.kevin.sample.networking.services.KevinApiService
import java.util.UUID

internal class KevinApi(
    private val service: KevinApiService
) {

    suspend fun fetchAuthState(request: AuthStateRequest): String {
        return service.fetchAuthState(request).state
    }

    suspend fun initiateBankPayment(request: InitiatePaymentRequest): UUID {
        val paymentId = service.initiateBankPayment(request).id
        return UUID.fromString(paymentId)
    }

    suspend fun initiateCardPayment(request: InitiatePaymentRequest): UUID {
        val paymentId = service.initiateCardPayment(request).id
        return UUID.fromString(paymentId)
    }
}