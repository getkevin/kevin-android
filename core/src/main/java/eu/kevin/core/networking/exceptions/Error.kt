package eu.kevin.core.networking.exceptions

import kotlinx.serialization.Serializable

@Serializable
data class Error(
    val code: Int,
    val name: String,
    val description: String
)