package eu.kevin.demo.screens.chooseaccount

import android.os.Parcelable
import eu.kevin.common.architecture.interfaces.IState
import eu.kevin.demo.data.database.entities.LinkedAccount
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class ChooseAccountState(
    val accounts: List<LinkedAccount> = emptyList()
) : IState, Parcelable