package eu.kevin.demo.auth.entities

import kotlinx.serialization.Serializable

@Serializable
internal data class ApiAuthState(
    val authorizationLink: String,
    val state: String
)