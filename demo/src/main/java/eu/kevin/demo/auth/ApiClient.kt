package eu.kevin.demo.auth

import eu.kevin.core.networking.BaseApiClient
import eu.kevin.demo.auth.entities.ApiPayment
import eu.kevin.demo.auth.entities.InitiateAuthenticationRequest
import eu.kevin.demo.auth.entities.InitiatePaymentRequest

interface ApiClient : BaseApiClient {
    suspend fun getAuthState(request: InitiateAuthenticationRequest): String
    suspend fun initializeBankPayment(request: InitiatePaymentRequest): ApiPayment
    suspend fun initializeLinkedBankPayment(accessToken: String, request: InitiatePaymentRequest): ApiPayment
    suspend fun initializeCardPayment(request: InitiatePaymentRequest): ApiPayment
    suspend fun initializeHybridPayment(request: InitiatePaymentRequest): ApiPayment
}