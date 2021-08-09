package eu.kevin.accounts.bankselection.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class BankListItem(
    val bankId: String,
    val title: String,
    val logoUrl: String,
    var isSelected: Boolean = false
) : Parcelable