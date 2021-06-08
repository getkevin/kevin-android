package eu.kevin.accounts.linkingsession

import eu.kevin.core.entities.ActivityResult

internal interface AccountLinkingSessionListener {
    fun onSessionFinished(sessionResult: ActivityResult<AccountLinkingResult>)
}