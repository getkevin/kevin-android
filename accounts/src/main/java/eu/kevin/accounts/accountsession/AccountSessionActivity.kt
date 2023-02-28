package eu.kevin.accounts.accountsession

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eu.kevin.accounts.KevinAccountsPlugin
import eu.kevin.accounts.R
import eu.kevin.accounts.accountsession.entities.AccountSessionConfiguration
import eu.kevin.accounts.databinding.KevinActivityAccountLinkingBinding
import eu.kevin.common.architecture.BaseFragmentActivity
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.extensions.setFragmentResult
import eu.kevin.core.entities.SessionResult
import eu.kevin.core.plugin.Kevin
import eu.kevin.core.plugin.KevinException
import kotlinx.coroutines.launch

class AccountSessionActivity : BaseFragmentActivity(), AccountSessionListener {

    private var accountSessionConfiguration: AccountSessionConfiguration? = null

    private lateinit var accountLinkingSession: AccountSession
    private lateinit var binding: KevinActivityAccountLinkingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!KevinAccountsPlugin.isConfigured()) {
            throw KevinException("Accounts plugin is not configured!")
        }
        setTheme(Kevin.getTheme())
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = KevinActivityAccountLinkingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        accountSessionConfiguration = intent?.extras?.getParcelable(AccountSessionContract.CONFIGURATION_KEY)

        if (accountSessionConfiguration == null) {
            finish()
            return
        }

        accountLinkingSession = AccountSession(
            supportFragmentManager,
            accountSessionConfiguration!!,
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let {
            accountLinkingSession.handleDeepLink(it)
        }
    }

    override fun returnActivityResult(result: SessionResult<*>) {
        setResult(Activity.RESULT_OK, Intent().putExtra(AccountSessionContract.RESULT_KEY, result))
        finish()
    }

    private fun showExitConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setTitle(R.string.kevin_dialog_exit_confirmation_title)
            .setMessage(R.string.kevin_dialog_exit_confirmation_accounts_message)
            .setPositiveButton(R.string.kevin_yes) { _, _ ->
                returnActivityResult(SessionResult.Canceled)
            }
            .setNegativeButton(R.string.kevin_no) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun startListeningForRouteRequests() {
        lifecycleScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    GlobalRouter.mainRouterFlow.collect { fragment ->
                        pushFragment(R.id.main_router_container, fragment)
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

    // AccountLinkingSessionListener

    override fun onSessionFinished(sessionResult: SessionResult<AccountSessionResult>) {
        returnActivityResult(sessionResult)
    }

    override fun showLoading(show: Boolean) {
        binding.loadingView.visibility = if (show) VISIBLE else GONE
    }
}