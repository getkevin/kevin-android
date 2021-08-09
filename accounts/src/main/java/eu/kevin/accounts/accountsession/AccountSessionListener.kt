package eu.kevin.accounts.accountsession

import eu.kevin.core.entities.SessionResult

internal interface AccountSessionListener {
    fun onSessionFinished(sessionResult: SessionResult<AccountSessionResult>)
    fun showLoading(show: Boolean)
}