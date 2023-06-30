package eu.kevin.accounts.accountlinking

import android.os.Bundle
import androidx.fragment.app.Fragment
import eu.kevin.common.fragment.FragmentResult
import eu.kevin.common.fragment.FragmentResultContract

object AccountLinkingContract : FragmentResultContract<FragmentResult<AccountLinkingFragmentResult>>() {
    override val requestKey = "account_linking_request_key"
    override val resultKey = "account_linking_result_key"

    fun getFragment(configuration: AccountLinkingFragmentConfiguration): Fragment {
        return AccountLinkingFragment().also {
            it.configuration = configuration
        }
    }

    override fun parseResult(data: Bundle): FragmentResult<AccountLinkingFragmentResult> {
        return data.getParcelable(resultKey)!!
    }
}