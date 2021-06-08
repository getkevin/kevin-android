package eu.kevin.demo.auth

import eu.kevin.core.networking.BaseApiClient
import eu.kevin.demo.auth.entities.ApiPayment

interface KevinAuthClient : BaseApiClient {
    suspend fun getAuthState(): String
    suspend fun initializeBankPayment(): ApiPayment
    suspend fun initializeCardPayment(): ApiPayment
}