package eu.kevin.core.networking.entities

import kotlinx.serialization.Serializable

/**
 * Data class representing basic network result containing list of some data
 * @property data list of data
 * @param T type of data
 */
@Serializable
data class KevinResponse<T>(
    val data: List<T>
)