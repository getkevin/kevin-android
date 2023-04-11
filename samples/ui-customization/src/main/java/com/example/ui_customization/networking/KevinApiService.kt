package com.example.ui_customization.networking

import com.example.ui_customization.networking.entities.AuthStateRequest
import com.example.ui_customization.networking.entities.AuthStateResponse
import retrofit2.http.Body
import retrofit2.http.POST

internal interface KevinApiService {

    @POST("auth/initiate/")
    suspend fun fetchAuthState(
        @Body request: AuthStateRequest,
    ): AuthStateResponse
}
