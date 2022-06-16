package eu.kevin.demo.screens.accountlinking

import eu.kevin.accounts.accountsession.AccountSessionResult
import eu.kevin.common.architecture.interfaces.IIntent
import eu.kevin.core.entities.SessionResult
import eu.kevin.demo.screens.accountactions.entities.AccountAction

internal sealed class AccountLinkingIntent : IIntent {
    object OnStartAccountLinking : AccountLinkingIntent()
    data class OnAccountLinkingResult(
        val accountSessionResult: SessionResult<AccountSessionResult>
    ) : AccountLinkingIntent()
    data class OnAccountActionSelected(val action: AccountAction) : AccountLinkingIntent()
    data class OpenMenuForAccount(val id: Long) : AccountLinkingIntent()
}