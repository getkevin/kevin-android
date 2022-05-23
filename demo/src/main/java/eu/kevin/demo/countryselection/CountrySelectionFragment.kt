package eu.kevin.demo.countryselection
import android.content.Context
import androidx.fragment.app.viewModels
import eu.kevin.common.architecture.BaseModalFragment
import eu.kevin.common.architecture.interfaces.IView

internal class CountrySelectionFragment :
    BaseModalFragment<CountrySelectionState, CountrySelectionIntent, CountrySelectionViewModel>(),
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
        viewModel.intents.trySend(CountrySelectionIntent.Initialize(configuration!!))
    }

    // CountriesViewDelegate

    override fun onCountryClicked(iso: String) {
        viewModel.intents.trySend(CountrySelectionIntent.HandleCountrySelection(iso))
        dismiss()
    }
}