package eu.kevin.accounts.networking.entities

import kotlinx.serialization.Serializable

/**
 * Data class representing Bank entity
 * @property id bank id
 * @property name bank name
 * @property officialName bank official name
 * @property countryCode country iso code to which bank belongs to
 * @property isSandbox tells if bank is in sandbox environment
 * @property imageUri uri to bank logo image
 * @property bic bank identification code
 * @property isBeta tells if bank is in beta stage
 * @property isAccountLinkingSupported tells if this bank can be used for account linking
 */
@Serializable
data class ApiBank(
    val id: String,
    val name: String,
    val officialName: String? = null,
    val countryCode: String,
    val isSandbox: Boolean,
    val imageUri: String,
    val bic: String? = null,
    val isBeta: Boolean,
    val isAccountLinkingSupported: Boolean
)