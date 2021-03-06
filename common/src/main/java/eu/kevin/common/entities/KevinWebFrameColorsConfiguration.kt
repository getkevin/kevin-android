package eu.kevin.common.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KevinWebFrameColorsConfiguration(
    @SerialName("bc")
    val backgroundColor: String,
    @SerialName("bsc")
    val baseColor: String,
    @SerialName("hc")
    val headingsColor: String,
    @SerialName("fc")
    val fontColor: String,
    @SerialName("bic")
    val bankIconColor: String,
    @SerialName("dbc")
    val defaultButtonColor: String
)