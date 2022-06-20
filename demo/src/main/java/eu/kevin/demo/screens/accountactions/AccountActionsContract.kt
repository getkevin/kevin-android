package eu.kevin.demo.screens.accountactions

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.kevin.common.fragment.FragmentResultContract
import eu.kevin.demo.screens.accountactions.entities.AccountAction

internal object AccountActionsContract : FragmentResultContract<AccountAction>() {
    override val requestKey = "choose_account_action_request_key"
    override val resultKey = "choose_account_action_result_key"

    fun getFragment(configuration: AccountActionsFragmentConfiguration): BottomSheetDialogFragment {
        return AccountActionsFragment().also {
            it.configuration = configuration
        }
    }

    override fun parseResult(data: Bundle): AccountAction {
        return data.getParcelable(resultKey)!!
    }
}