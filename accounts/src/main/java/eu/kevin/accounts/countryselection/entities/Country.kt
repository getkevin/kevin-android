package eu.kevin.accounts.countryselection.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class Country(
    val iso: String,
    val title: String? = null,
    var isSelected: Boolean = false
) : Parcelable