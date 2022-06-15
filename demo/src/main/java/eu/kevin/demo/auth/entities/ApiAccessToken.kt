package eu.kevin.demo.auth.entities

import kotlinx.serialization.Serializable

@Serializable
internal data class ApiAccessToken(
    val tokenType: String,
    val accessToken: String,
    val accessTokenExpiresIn: Long,
    val refreshToken: String,
    val refreshTokenExpiresIn: Long
)