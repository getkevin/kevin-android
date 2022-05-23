package eu.kevin.inapppayments.paymentsession

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import eu.kevin.core.entities.SessionResult
import eu.kevin.inapppayments.paymentsession.entities.PaymentSessionConfiguration

/**
 * Activity result contract for payment session. This is an entry point for payment flow.
 * This contract takes in [PaymentSessionConfiguration] and returns [PaymentSessionResult] on success
 * This contract will also produce different results, you can see them in [SessionResult]
 */
class PaymentSessionContract :
    ActivityResultContract<PaymentSessionConfiguration, SessionResult<PaymentSessionResult>>() {

    override fun createIntent(context: Context, config: PaymentSessionConfiguration): Intent {
        val intent = Intent(context, PaymentSessionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra(CONFIGURATION_KEY, config)
        return intent
    }

    override fun parseResult(resultCode: Int, result: Intent?): SessionResult<PaymentSessionResult> {
        return if (resultCode == Activity.RESULT_OK) {
            try {
                result?.extras?.getParcelable<SessionResult<PaymentSessionResult>>(RESULT_KEY)!!
            } catch (error: Exception) {
                SessionResult.Failure(error)
            }
        } else {
            SessionResult.Failure(UnknownError("Activity returned unknown resultCode"))
        }
    }

    companion object {
        const val CONFIGURATION_KEY = "configuration_key"
        const val RESULT_KEY = "result_key"
    }
}