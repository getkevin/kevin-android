package eu.kevin.common.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KevinWebFrameConfiguration(
    @SerialName("cl")
    val customLayout: List<String>,
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
    val buttonColor: String,
    @SerialName("dbfc")
    val buttonFontColor: String,
    @SerialName("br")
    val buttonRadius: String,
    @SerialName("ibc")
    val inputBorderColor: String
)