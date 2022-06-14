package eu.kevin.demo.auth

import eu.kevin.demo.auth.entities.ApiAccessToken
import eu.kevin.demo.auth.entities.ApiAuthState
import eu.kevin.demo.auth.entities.ApiPayment
import eu.kevin.demo.auth.entities.InitiateAuthenticationRequest
import eu.kevin.demo.auth.entities.InitiatePaymentRequest
import eu.kevin.demo.auth.entities.RefreshAccessTokenRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post

class KevinClient(private val httpClient: HttpClient) : KevinApiClient {

    override suspend fun getAccessToken(authorizationCode: String): ApiAccessToken {
        return httpClient.get("auth/tokens/") {
            parameter("authorizationCode", authorizationCode)
        }
    }

    override suspend fun refreshAccessToken(request: RefreshAccessTokenRequest): ApiAccessToken {
        return httpClient.post("auth/refreshToken/") {
            body = request
        }
    }

    override suspend fun getAuthState(request: InitiateAuthenticationRequest): String {
        return httpClient.post<ApiAuthState>("auth/initiate/") {
            body = request
        }.state
    }

    override suspend fun getCardAuthState(): String {
        return httpClient.post<ApiAuthState>("auth/initiate/card/").state
    }

    override suspend fun initializeBankPayment(
        request: InitiatePaymentRequest
    ): ApiPayment {
        return httpClient.post("payments/bank/") {
            body = request
        }
    }

    override suspend fun initializeLinkedBankPayment(
        accessToken: String,
        request: InitiatePaymentRequest
    ): ApiPayment {
        return httpClient.post("payments/bank/linked/") {
            header("Authorization", "Bearer $accessToken")
            body = request
        }
    }

    override suspend fun initializeCardPayment(
        request: InitiatePaymentRequest
    ): ApiPayment {
        return httpClient.post("payments/card/") {
            body = request
        }
    }
}