package eu.kevin.accounts.accountsession

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import eu.kevin.accounts.accountsession.entities.AccountSessionConfiguration
import eu.kevin.core.entities.SessionResult

/**
 * Activity result contract for account linking. This is an entry point for account linking flow.
 * This contract takes in [AccountSessionConfiguration] and returns [AccountSessionResult] on success
 * This contract will also produce different results, you can see them in [SessionResult]
 */
class AccountSessionContract :
    ActivityResultContract<AccountSessionConfiguration, SessionResult<AccountSessionResult>>() {

    override fun createIntent(context: Context, config: AccountSessionConfiguration): Intent {
        val intent = Intent(context, AccountSessionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra(CONFIGURATION_KEY, config)
        return intent
    }

    override fun parseResult(resultCode: Int, result: Intent?): SessionResult<AccountSessionResult> {
        return if (resultCode == Activity.RESULT_OK) {
            try {
                result?.extras?.getParcelable<SessionResult<AccountSessionResult>>(RESULT_KEY)!!
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