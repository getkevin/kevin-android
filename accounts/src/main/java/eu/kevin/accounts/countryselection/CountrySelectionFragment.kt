package eu.kevin.accounts.countryselection

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.viewModels
import eu.kevin.accounts.countryselection.CountrySelectionIntent.*
import eu.kevin.core.architecture.BaseModalFragment
import eu.kevin.core.architecture.interfaces.IView
import eu.kevin.core.fragment.FragmentResultContract

class CountrySelectionFragment : BaseModalFragment<CountrySelectionState, CountrySelectionIntent, CountrySelectionViewModel>(),
    CountrySelectionViewDelegate {

    var configuration: CountrySelectionFragmentConfiguration? by savedState()

    override val viewModel: CountrySelectionViewModel by viewModels {
        CountrySelectionViewModel.Factory(this)
    }

    override fun onCreateView(context: Context): IView<CountrySelectionState> {
        return CountrySelectionView(context).also {
            it.delegate = this
        }
    }

    override fun onAttached() {
        viewModel.intents.trySend(Initialize(configuration!!))
    }

    // CountriesViewDelegate

    override fun onCountryClicked(iso: String) {
        viewModel.intents.trySend(HandleCountrySelection(iso))
        dismiss()
    }

    object Contract: FragmentResultContract<String>() {
        override val requestKey = "country_selection_request_key"
        override val resultKey = "country_selection_result_key"
        override fun parseResult(data: Bundle): String {
            return data.getString(resultKey) ?: ""
        }
    }
}