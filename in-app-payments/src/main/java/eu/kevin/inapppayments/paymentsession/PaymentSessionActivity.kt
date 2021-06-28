package eu.kevin.inapppayments.paymentsession

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eu.kevin.core.architecture.BaseFragmentActivity
import eu.kevin.core.architecture.routing.GlobalRouter
import eu.kevin.core.entities.ActivityResult
import eu.kevin.core.extensions.setFragmentResult
import eu.kevin.core.plugin.Kevin
import eu.kevin.core.plugin.KevinException
import eu.kevin.inapppayments.KevinPaymentsPlugin
import eu.kevin.inapppayments.R
import eu.kevin.inapppayments.paymentsession.entities.PaymentSessionConfiguration
import kotlinx.coroutines.launch

internal class PaymentSessionActivity : BaseFragmentActivity(), PaymentSessionListener {

    private var paymentSessionConfiguration: PaymentSessionConfiguration? = null

    private lateinit var paymentSession: PaymentSession

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!KevinPaymentsPlugin.isConfigured()) {
            throw KevinException("Payments plugin is not configured!")
        }
        setTheme(Kevin.getTheme())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_session)

        paymentSessionConfiguration = intent?.extras?.getParcelable(PaymentSessionContract.CONFIGURATION_KEY)

        paymentSession = PaymentSession(
            supportFragmentManager,
            paymentSessionConfiguration!!,
            this,
            this
        )

        startListeningForRouteRequests()
    }

    override fun onStart() {
        super.onStart()
        paymentSession.beginFlow(this)
    }

    override fun handleBack() {
        with(supportFragmentManager) {
            if (backStackEntryCount == 1) {
                showExitConfirmation()
            } else {
                popBackStack()
            }
        }
    }

    private fun showExitConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setTitle(eu.kevin.accounts.R.string.dialog_exit_confirmation_title)
            .setMessage(R.string.dialog_exit_confirmation_payments_message)
            .setPositiveButton(eu.kevin.accounts.R.string.yes) { _, _ ->
                returnActivityResult(ActivityResult.Canceled)
            }
            .setNegativeButton(eu.kevin.accounts.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun returnActivityResult(result: ActivityResult<*>) {
        setResult(Activity.RESULT_OK, Intent().putExtra(PaymentSessionContract.RESULT_KEY, result))
        finish()
    }

    private fun startListeningForRouteRequests() {
        lifecycleScope.launch {
            GlobalRouter.addOnPushFragmentListener(this) { fragment ->
                pushFragment(R.id.main_router_container, fragment)
            }

            GlobalRouter.addOnPushModalFragmentListener(this) { modalFragment ->
                modalFragment.show(supportFragmentManager, modalFragment::class.simpleName)
            }

            GlobalRouter.addOnPopCurrentFragmentListener(this) {
                handleBack()
            }

            GlobalRouter.addOnReturnFragmentResultListener(this) { result ->
                supportFragmentManager.setFragmentResult(result.contract, result.data)
            }
        }
    }

    // PaymentSessionListener

    override fun onSessionFinished(sessionResult: ActivityResult<PaymentSessionResult>) {
        returnActivityResult(sessionResult)
    }
}