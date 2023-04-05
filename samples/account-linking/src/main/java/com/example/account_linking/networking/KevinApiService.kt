package com.example.account_linking.networking

import com.example.account_linking.networking.entities.AuthStateRequest
import com.example.account_linking.networking.entities.AuthStateResponse
import retrofit2.http.Body
import retrofit2.http.POST

internal interface KevinApiService {

    @POST("auth/initiate/")
    suspend fun fetchAuthState(
        @Body request: AuthStateRequest,
    ): AuthStateResponse
}
