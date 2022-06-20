package eu.kevin.demo.screens.accountactions

import eu.kevin.common.architecture.interfaces.IIntent

internal sealed class AccountActionsIntent : IIntent {
    data class HandleRemoveAccount(val id: Long) : AccountActionsIntent()
}