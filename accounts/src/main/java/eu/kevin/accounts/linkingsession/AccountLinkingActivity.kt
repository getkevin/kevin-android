package eu.kevin.accounts.linkingsession

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eu.kevin.accounts.KevinAccountsPlugin
import eu.kevin.accounts.R
import eu.kevin.accounts.databinding.ActivityAccountLinkingBinding
import eu.kevin.accounts.linkingsession.entities.AccountLinkingConfiguration
import eu.kevin.core.architecture.BaseFragmentActivity
import eu.kevin.core.architecture.routing.GlobalRouter
import eu.kevin.core.entities.ActivityResult
import eu.kevin.core.extensions.setFragmentResult
import eu.kevin.core.plugin.Kevin
import eu.kevin.core.plugin.KevinException
import kotlinx.coroutines.launch

class AccountLinkingActivity : BaseFragmentActivity(), AccountLinkingSessionListener {

    private var accountLinkingConfiguration: AccountLinkingConfiguration? = null

    private lateinit var accountLinkingSession: AccountLinkingSession
    private lateinit var binding: ActivityAccountLinkingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!KevinAccountsPlugin.isConfigured()) {
            throw KevinException("Accounts plugin is not configured!")
        }
        setTheme(Kevin.getTheme())
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityAccountLinkingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        accountLinkingConfiguration = intent?.extras?.getParcelable(LinkAccountContract.CONFIGURATION_KEY)

        accountLinkingSession = AccountLinkingSession(
            supportFragmentManager,
            accountLinkingConfiguration!!,
            this,
             this
        )
        startListeningForRouteRequests()
    }

    override fun onStart() {
        super.onStart()
        accountLinkingSession.beginFlow(this)
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
            .setTitle(R.string.dialog_exit_confirmation_title)
            .setMessage(R.string.dialog_exit_confirmation_accounts_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                returnActivityResult(ActivityResult.Canceled)
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun returnActivityResult(result: ActivityResult<*>) {
        setResult(Activity.RESULT_OK, Intent().putExtra(LinkAccountContract.RESULT_KEY, result))
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

    // AccountLinkingSessionListener

    override fun onSessionFinished(sessionResult: ActivityResult<AccountLinkingResult>) {
        returnActivityResult(sessionResult)
    }

    override fun showLoading(show: Boolean) {
        binding.loadingView.visibility = if (show) VISIBLE else GONE
    }
}