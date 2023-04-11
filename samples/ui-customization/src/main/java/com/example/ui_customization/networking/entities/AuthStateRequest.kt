package com.example.ui_customization.networking.entities

internal data class AuthStateRequest(
    val scopes: List<String>,
    val redirectUrl: String
)