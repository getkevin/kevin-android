package eu.kevin.accounts.bankselection

import android.os.Bundle
import androidx.fragment.app.Fragment
import eu.kevin.accounts.bankselection.entities.Bank
import eu.kevin.common.fragment.FragmentResultContract

object BankSelectionContract : FragmentResultContract<Bank>() {
    override val requestKey = "bank_selection_request_key"
    override val resultKey = "bank_selection_result_key"

    fun getFragment(configuration: BankSelectionFragmentConfiguration): Fragment {
        return BankSelectionFragment().also {
            it.configuration = configuration
        }
    }
    override fun parseResult(data: Bundle): Bank {
        return data.getParcelable(resultKey)!!
    }
}