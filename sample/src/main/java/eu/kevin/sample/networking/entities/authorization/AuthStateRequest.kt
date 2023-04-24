package eu.kevin.sample.networking.entities.authorization

internal data class AuthStateRequest(
    val scopes: List<String>,
    val redirectUrl: String
)