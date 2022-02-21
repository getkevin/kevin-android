package eu.kevin.demo.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class GetCountriesResponse(
    val data: List<String>
)
