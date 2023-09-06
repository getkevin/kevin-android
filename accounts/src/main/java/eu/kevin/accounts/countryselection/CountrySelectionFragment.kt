package eu.kevin.accounts.countryselection

import android.content.Context
import androidx.fragment.app.viewModels
import eu.kevin.accounts.countryselection.CountrySelectionIntent.HandleCountrySelection
import eu.kevin.accounts.countryselection.CountrySelectionIntent.Initialize
import eu.kevin.common.architecture.BaseModalFragment
import eu.kevin.common.architecture.interfaces.IView

internal class CountrySelectionFragment :
    BaseModalFragment<CountrySelectionState, CountrySelectionIntent, CountrySelectionViewModel>(),
    CountrySelectionViewDelegate {

    var configuration: CountrySelectionFragmentConfiguration? by savedState()

    override val viewModel: CountrySelectionViewModel by viewModels {
        CountrySelectionViewModel.Factory
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
}