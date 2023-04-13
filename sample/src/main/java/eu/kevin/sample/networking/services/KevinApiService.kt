package eu.kevin.sample.networking.services

import eu.kevin.sample.networking.entities.authorization.AuthStateRequest
import eu.kevin.sample.networking.entities.authorization.AuthStateResponse
import eu.kevin.sample.networking.entities.payments.InitiatePaymentRequest
import eu.kevin.sample.networking.entities.payments.InitiatePaymentResponse
import retrofit2.http.Body
import retrofit2.http.POST

internal interface KevinApiService {

    @POST("auth/initiate/")
    suspend fun fetchAuthState(
        @Body request: AuthStateRequest,
    ): AuthStateResponse

    @POST("payments/bank/")
    suspend fun initiateBankPayment(
        @Body request: InitiatePaymentRequest,
    ): InitiatePaymentResponse

    @POST("payments/card/")
    suspend fun initiateCardPayment(
        @Body request: InitiatePaymentRequest,
    ): InitiatePaymentResponse
}
