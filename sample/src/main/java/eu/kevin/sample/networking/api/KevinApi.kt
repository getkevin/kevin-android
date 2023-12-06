package eu.kevin.sample.networking.api

import eu.kevin.sample.networking.entities.authorization.AuthStateRequest
import eu.kevin.sample.networking.entities.payments.InitiatePaymentRequest
import eu.kevin.sample.networking.services.KevinApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class KevinApi(
    private val service: KevinApiService
) {

    suspend fun fetchAuthState(request: AuthStateRequest): String {
        return withContext(Dispatchers.IO) {
            service.fetchAuthState(request).state
        }
    }

    suspend fun initiateBankPayment(request: InitiatePaymentRequest): String {
        return withContext(Dispatchers.IO) {
            service.initiateBankPayment(request).id
        }
    }
}