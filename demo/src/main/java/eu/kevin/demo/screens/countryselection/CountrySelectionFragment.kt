package eu.kevin.demo.screens.countryselection

import android.content.Context
import androidx.fragment.app.viewModels
import eu.kevin.common.architecture.BaseModalFragment
import eu.kevin.common.architecture.interfaces.View
import eu.kevin.demo.screens.countryselection.CountrySelectionIntent.HandleCountrySelection
import eu.kevin.demo.screens.countryselection.CountrySelectionIntent.Initialize

internal class CountrySelectionFragment :
    BaseModalFragment<CountrySelectionState, CountrySelectionIntent, CountrySelectionViewModel>(),
    CountrySelectionViewDelegate {

    var configuration: CountrySelectionFragmentConfiguration? by savedState()

    override val viewModel: CountrySelectionViewModel by viewModels {
        CountrySelectionViewModel.Factory(this)
    }

    override fun onCreateView(context: Context): View<CountrySelectionState> {
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