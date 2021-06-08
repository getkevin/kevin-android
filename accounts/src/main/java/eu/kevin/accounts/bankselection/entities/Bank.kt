package eu.kevin.accounts.bankselection.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Bank(
    val bankId: String,
    val title: String,
    val logoUrl: String,
    var isSelected: Boolean = false
) : Parcelable