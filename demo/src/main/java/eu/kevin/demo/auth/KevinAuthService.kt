package eu.kevin.demo.auth

import eu.kevin.demo.auth.entities.ApiAuthState
import eu.kevin.demo.auth.entities.ApiPayment
import retrofit2.http.GET

interface KevinAuthService {
    @GET("examples/auth_example")
    suspend fun getAuthState(): ApiAuthState

    @GET("examples/bank_card_example")
    suspend fun initializeBankPayment(): ApiPayment

    @GET("examples/card_example")
    suspend fun initializeCardPayment(): ApiPayment
}