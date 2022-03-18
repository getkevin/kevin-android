package eu.kevin.core.networking.exceptions

import kotlinx.serialization.Serializable

@Serializable
internal data class ErrorResponse(
    val error: Error
)