package eu.kevin.demo.screens.payment.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class CreditorListItem(
    val logo: String,
    val name: String,
    val iban: String,
    val isSelected: Boolean = false
) : Parcelable