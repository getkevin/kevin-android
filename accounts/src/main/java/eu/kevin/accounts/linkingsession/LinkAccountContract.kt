package eu.kevin.accounts.linkingsession

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import eu.kevin.accounts.linkingsession.entities.AccountLinkingConfiguration
import eu.kevin.core.entities.ActivityResult

/**
 * Activity result contract for account linking. This is an entry point for account linking flow.
 * This contract takes in [AccountLinkingConfiguration] and returns [AccountLinkingResult] on success
 * This contract will also produce different results, you can see them in [ActivityResult]
 */
class LinkAccountContract : ActivityResultContract<AccountLinkingConfiguration?, ActivityResult<AccountLinkingResult>>() {
    override fun createIntent(context: Context, config: AccountLinkingConfiguration?): Intent {
        val intent = Intent(context, AccountLinkingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra(CONFIGURATION_KEY, config)
        return intent
    }

    override fun parseResult(resultCode: Int, result: Intent?) : ActivityResult<AccountLinkingResult> {
        return result?.extras?.getParcelable(RESULT_KEY)!!
    }

    internal companion object {
        const val CONFIGURATION_KEY = "configuration_key"
        const val RESULT_KEY = "result_key"
    }
}