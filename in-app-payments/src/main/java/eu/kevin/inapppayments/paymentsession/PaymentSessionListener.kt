package eu.kevin.inapppayments.paymentsession

import eu.kevin.core.entities.SessionResult

internal interface PaymentSessionListener {
    fun onSessionFinished(sessionResult: SessionResult<PaymentSessionResult>)
    fun showLoading(show: Boolean)
}