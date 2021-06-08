package eu.kevin.accounts.linkingsession

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import eu.kevin.accounts.KevinAccountsPlugin
import eu.kevin.accounts.R
import eu.kevin.accounts.linkingsession.entities.AccountLinkingConfiguration
import eu.kevin.core.architecture.BaseFragmentActivity
import eu.kevin.core.architecture.routing.GlobalRouter
import eu.kevin.core.entities.ActivityResult
import eu.kevin.core.extensions.setFragmentResult
import eu.kevin.core.plugin.KevinException
import kotlinx.coroutines.launch

internal class AccountLinkingActivity : BaseFragmentActivity(), AccountLinkingSessionListener {

    private var accountLinkingConfiguration: AccountLinkingConfiguration? = null
    private lateinit var accountLinkingSession: AccountLinkingSession

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!KevinAccountsPlugin.isConfigured()) {
            throw KevinException("Accounts plugin is not configured!")
        }
        setTheme(KevinAccountsPlugin.getTheme())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_linking)

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
}