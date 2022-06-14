package eu.kevin.demo.preferences

import kotlinx.serialization.Serializable

@Serializable
data class AccessToken(
    val tokenType: String,
    val accessToken: String,
    val accessTokenExpiresAt: Long,
    val refreshToken: String,
    val refreshTokenExpiresAt: Long
)