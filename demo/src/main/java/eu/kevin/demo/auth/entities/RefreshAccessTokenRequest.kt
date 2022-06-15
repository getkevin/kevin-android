package eu.kevin.demo.auth.entities

import kotlinx.serialization.Serializable

@Serializable
internal data class RefreshAccessTokenRequest(
    val refreshToken: String
)