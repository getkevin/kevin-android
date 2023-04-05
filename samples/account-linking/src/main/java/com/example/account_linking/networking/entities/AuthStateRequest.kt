package com.example.account_linking.networking.entities

internal data class AuthStateRequest(
    val scopes: List<String>,
    val redirectUrl: String
)