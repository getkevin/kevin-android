package eu.kevin.accounts.countryselection

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import eu.kevin.accounts.countryselection.adapters.CountryListAdapter
import eu.kevin.accounts.countryselection.entities.Country
import eu.kevin.accounts.countryselection.helpers.CountryHelper
import eu.kevin.accounts.databinding.KevinFragmentCountrySelectionBinding
import eu.kevin.common.architecture.BaseView
import eu.kevin.common.architecture.interfaces.View
import eu.kevin.common.entities.LoadingState
import eu.kevin.common.entities.isLoading
import eu.kevin.common.extensions.applySystemInsetsPadding
import eu.kevin.common.extensions.fadeIn
import eu.kevin.common.extensions.fadeOut
import eu.kevin.common.helpers.ErrorHelper
import eu.kevin.common.helpers.SnackbarHelper

internal class CountrySelectionView(context: Context) :
    BaseView<KevinFragmentCountrySelectionBinding>(context),
    View<CountrySelectionState> {

    override val binding = KevinFragmentCountrySelectionBinding.inflate(LayoutInflater.from(context), this)

    var delegate: CountrySelectionViewDelegate? = null

    private val countriesAdapter = CountryListAdapter {
        delegate?.onCountryClicked(it)
    }

    init {
        binding.countriesRecyclerView.applySystemInsetsPadding(bottom = true)
        with(binding.countriesRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = countriesAdapter
        }
    }

    override fun render(state: CountrySelectionState) = with(binding) {
        when (state.loadingState) {
            is LoadingState.Loading -> showLoading(state.loadingState.isLoading())
            is LoadingState.FailureWithMessage -> showError(state.loadingState.message)
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
        with(binding) {
            if (isLoading) {
                progressView.fadeIn()
            } else {
                progressView.fadeOut()
            }
        }
    }

    private fun showError(message: String) {
        binding.progressView.fadeOut()
        SnackbarHelper.showError(this, message)
    }
}