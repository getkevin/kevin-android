package eu.kevin.demo.screens.accountactions

import eu.kevin.common.architecture.interfaces.Intent

internal sealed class AccountActionsIntent : Intent {
    data class HandleRemoveAccount(val id: Long) : AccountActionsIntent()
}