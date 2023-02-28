package eu.kevin.inapppayments.paymentsession

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eu.kevin.common.architecture.BaseFragmentActivity
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.extensions.setFragmentResult
import eu.kevin.core.entities.SessionResult
import eu.kevin.core.plugin.Kevin
import eu.kevin.core.plugin.KevinException
import eu.kevin.inapppayments.KevinPaymentsPlugin
import eu.kevin.inapppayments.R
import eu.kevin.inapppayments.databinding.KevinActivityPaymentSessionBinding
import eu.kevin.inapppayments.paymentsession.entities.PaymentSessionConfiguration
import kotlinx.coroutines.launch

class PaymentSessionActivity : BaseFragmentActivity(), PaymentSessionListener {

    private var paymentSessionConfiguration: PaymentSessionConfiguration? = null

    private lateinit var paymentSession: PaymentSession
    private lateinit var binding: KevinActivityPaymentSessionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!KevinPaymentsPlugin.isConfigured()) {
            throw KevinException("Payments plugin is not configured!")
        }
        setTheme(Kevin.getTheme())
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = KevinActivityPaymentSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        paymentSessionConfiguration = intent?.extras?.getParcelable(PaymentSessionContract.CONFIGURATION_KEY)

        if (paymentSessionConfiguration == null) {
            finish()
            return
        }

        paymentSession = PaymentSession(
            supportFragmentManager,
            paymentSessionConfiguration!!,
            this,
            this
        )

        startListeningForRouteRequests()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let {
            paymentSession.handleDeepLink(it)
        }
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

    override fun returnActivityResult(result: SessionResult<*>) {
        setResult(Activity.RESULT_OK, Intent().putExtra(PaymentSessionContract.RESULT_KEY, result))
        finish()
    }

    private fun showExitConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setTitle(eu.kevin.accounts.R.string.kevin_dialog_exit_confirmation_title)
            .setMessage(R.string.kevin_dialog_exit_confirmation_payments_message)
            .setPositiveButton(eu.kevin.accounts.R.string.kevin_yes) { _, _ ->
                returnActivityResult(SessionResult.Canceled)
            }
            .setNegativeButton(eu.kevin.accounts.R.string.kevin_no) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun startListeningForRouteRequests() {
        lifecycleScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    GlobalRouter.mainRouterFlow.collect { fragment ->
                        pushFragment(eu.kevin.accounts.R.id.main_router_container, fragment)
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    GlobalRouter.mainModalRouterFlow.collect { modalFragment ->
                        modalFragment.show(supportFragmentManager, modalFragment::class.simpleName)
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    GlobalRouter.popFragmentFlow.collect {
                        handleBack()
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    GlobalRouter.fragmentResultFlow.collect { result ->
                        supportFragmentManager.setFragmentResult(result.contract, result.data)
                    }
                }
            }
        }
    }

    // PaymentSessionListener

    override fun onSessionFinished(sessionResult: SessionResult<PaymentSessionResult>) {
        returnActivityResult(sessionResult)
    }

    override fun showLoading(show: Boolean) {
        binding.loadingView.visibility = if (show) View.VISIBLE else View.GONE
    }
}