package eu.kevin.demo.auth

import eu.kevin.core.networking.BaseApiClient
import eu.kevin.demo.auth.entities.ApiAccessToken
import eu.kevin.demo.auth.entities.ApiPayment
import eu.kevin.demo.auth.entities.InitiateAuthenticationRequest
import eu.kevin.demo.auth.entities.InitiatePaymentRequest
import eu.kevin.demo.auth.entities.RefreshAccessTokenRequest

internal interface KevinApiClient : BaseApiClient {
    suspend fun getAccessToken(authorizationCode: String): ApiAccessToken
    suspend fun refreshAccessToken(request: RefreshAccessTokenRequest): ApiAccessToken
    suspend fun getAuthState(request: InitiateAuthenticationRequest): String
    suspend fun getCardAuthState(): String
    suspend fun initializeBankPayment(request: InitiatePaymentRequest): ApiPayment
    suspend fun initializeLinkedBankPayment(accessToken: String, request: InitiatePaymentRequest): ApiPayment
    suspend fun initializeCardPayment(request: InitiatePaymentRequest): ApiPayment
}