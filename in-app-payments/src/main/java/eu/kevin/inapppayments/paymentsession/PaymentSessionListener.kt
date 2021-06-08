package eu.kevin.inapppayments.paymentsession

import eu.kevin.core.entities.ActivityResult

internal interface PaymentSessionListener {
    fun onSessionFinished(sessionResult: ActivityResult<PaymentSessionResult>)
}