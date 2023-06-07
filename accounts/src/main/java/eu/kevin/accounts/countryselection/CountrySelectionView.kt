package eu.kevin.accounts.countryselection

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import eu.kevin.accounts.countryselection.adapters.CountryListAdapter
import eu.kevin.accounts.countryselection.entities.Country
import eu.kevin.accounts.countryselection.helpers.CountryHelper
import eu.kevin.accounts.databinding.KevinFragmentCountrySelectionBinding
import eu.kevin.common.architecture.BaseView
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.entities.LoadingState
import eu.kevin.common.entities.isLoading
import eu.kevin.common.extensions.applySystemInsetsPadding
import eu.kevin.common.extensions.fadeIn
import eu.kevin.common.extensions.fadeOut
import eu.kevin.common.helpers.ErrorHelper
import eu.kevin.common.helpers.SnackbarHelper

internal class CountrySelectionView(context: Context) :
    BaseView<KevinFragmentCountrySelectionBinding>(context),
    IView<CountrySelectionState> {

    override var binding: KevinFragmentCountrySelectionBinding? = KevinFragmentCountrySelectionBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    var delegate: CountrySelectionViewDelegate? = null

    private val countriesAdapter = CountryListAdapter {
        delegate?.onCountryClicked(it)
    }

    init {
        with(requireBinding().countriesRecyclerView) {
            applySystemInsetsPadding(bottom = true)
            layoutManager = LinearLayoutManager(context)
            adapter = countriesAdapter
        }
    }

    override fun render(state: CountrySelectionState) = with(requireBinding()) {
        when (state.loadingState) {
            is LoadingState.Loading -> showLoading(state.loadingState.isLoading())
            is LoadingState.Failure -> {
                showError(ErrorHelper.getMessage(context, state.loadingState.error))
            }
            null -> showLoading(false)
        }
        val countries = state.supportedCountries.map {
            Country(
                it.iso,
                CountryHelper.getCountryName(context, it.iso),
                it.isSelected
            )
        }.sortedBy { it.title }
        countriesAdapter.updateItems(countries)
    }

    private fun showLoading(isLoading: Boolean) {
        with(requireBinding()) {
            if (isLoading) {
                progressView.fadeIn()
            } else {
                progressView.fadeOut()
            }
        }
    }

    private fun showError(message: String) {
        requireBinding().progressView.fadeOut()
        SnackbarHelper.showError(this, message)
    }
}