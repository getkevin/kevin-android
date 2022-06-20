package eu.kevin.demo.auth.entities

import kotlinx.serialization.Serializable

@Serializable
internal data class InitiateAuthenticationRequest(
    val scopes: List<String>,
    val redirectUrl: String
)