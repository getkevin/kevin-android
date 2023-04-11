package com.example.ui_customization.networking

import com.example.ui_customization.networking.entities.AuthStateRequest

internal class KevinApi(
    private val service: KevinApiService
) {

    suspend fun fetchAuthState(request: AuthStateRequest): String {
        return service.fetchAuthState(request).state
    }
}