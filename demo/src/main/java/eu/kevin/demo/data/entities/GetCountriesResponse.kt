package eu.kevin.demo.data.entities

import kotlinx.serialization.Serializable

@Serializable
internal data class GetCountriesResponse(
    val data: List<String>
)