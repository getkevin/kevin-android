package eu.kevin.accounts.countryselection

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.kevin.common.fragment.FragmentResultContract

object CountrySelectionContract : FragmentResultContract<String>() {
    override val requestKey = "country_selection_request_key"
    override val resultKey = "country_selection_result_key"

    fun getFragment(configuration: CountrySelectionFragmentConfiguration): BottomSheetDialogFragment {
        return CountrySelectionFragment().also {
            it.configuration = configuration
        }
    }
    override fun parseResult(data: Bundle): String {
        return data.getString(resultKey) ?: ""
    }
}