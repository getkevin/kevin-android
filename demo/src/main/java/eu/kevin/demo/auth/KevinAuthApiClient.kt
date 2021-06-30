package eu.kevin.demo.auth

import eu.kevin.demo.auth.entities.ApiAuthState
import eu.kevin.demo.auth.entities.ApiPayment
import io.ktor.client.*
import io.ktor.client.request.*

class KevinAuthApiClient(private val httpClient: HttpClient) : KevinAuthClient {

    override suspend fun getAuthState(): String {
        return httpClient.get<ApiAuthState>("examples/auth_example").state
    }

    override suspend fun initializeBankPayment(): ApiPayment {
        return httpClient.get("examples/bank_card_example")
    }

    override suspend fun initializeCardPayment(): ApiPayment {
        return httpClient.get("examples/card_example")
    }
}