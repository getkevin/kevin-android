package eu.kevin.accounts.bankselection.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class representing linked Bank entity
 * @property id bank id
 * @property name bank name
 * @property officialName bank official name
 * @property imageUri uri to bank logo image
 * @property bic bank identification code
 */
@Parcelize
data class Bank(
    val id: String,
    val name: String,
    val officialName: String? = null,
    val imageUri: String,
    val bic: String? = null
) : Parcelable