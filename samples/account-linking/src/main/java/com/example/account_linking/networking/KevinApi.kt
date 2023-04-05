package com.example.account_linking.networking

import com.example.account_linking.networking.entities.AuthStateRequest

internal class KevinApi(
    private val service: KevinApiService
) {

    suspend fun fetchAuthState(request: AuthStateRequest): String {
        return service.fetchAuthState(request).state
    }
}