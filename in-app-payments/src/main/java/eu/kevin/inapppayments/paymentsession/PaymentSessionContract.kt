package eu.kevin.inapppayments.paymentsession

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import eu.kevin.core.entities.ActivityResult
import eu.kevin.inapppayments.paymentsession.entities.PaymentSessionConfiguration

/**
 * Activity result contract for payment session. This is an entry point for payment flow.
 * This contract takes in [PaymentSessionConfiguration] and returns [PaymentSessionResult] on success
 * This contract will also produce different results, you can see them in [ActivityResult]
 */
class PaymentSessionContract : ActivityResultContract<PaymentSessionConfiguration, ActivityResult<PaymentSessionResult>>() {
    override fun createIntent(context: Context, config: PaymentSessionConfiguration): Intent {
        val intent = Intent(context, PaymentSessionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra(CONFIGURATION_KEY, config)
        return intent
    }

    override fun parseResult(resultCode: Int, result: Intent?) : ActivityResult<PaymentSessionResult> {
        return result?.extras?.getParcelable(RESULT_KEY)!!
    }

    companion object {
        const val CONFIGURATION_KEY = "configuration_key"
        const val RESULT_KEY = "result_key"
    }
}