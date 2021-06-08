package eu.kevin.demo.auth

import eu.kevin.core.networking.executeRequest

class KevinAuthApiClient(private val service: KevinAuthService) : KevinAuthClient {

    override suspend fun getAuthState() = executeRequest {
        service.getAuthState().state
    }

    override suspend fun initializeBankPayment() = executeRequest {
        service.initializeBankPayment()
    }

    override suspend fun initializeCardPayment() = executeRequest {
        service.initializeCardPayment()
    }
}