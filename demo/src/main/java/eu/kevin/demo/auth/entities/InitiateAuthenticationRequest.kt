package eu.kevin.demo.auth.entities

import kotlinx.serialization.Serializable

@Serializable
data class InitiateAuthenticationRequest(
    val scopes: List<String>,
    val redirectUrl: String
)