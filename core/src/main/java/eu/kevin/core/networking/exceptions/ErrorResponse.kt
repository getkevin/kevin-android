package eu.kevin.core.networking.exceptions

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: Error
)