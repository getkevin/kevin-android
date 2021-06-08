package eu.kevin.demo.auth.entities

import kotlinx.serialization.Serializable

@Serializable
data class ApiAuthState(
    val authorizationLink: String,
    val state: String
)