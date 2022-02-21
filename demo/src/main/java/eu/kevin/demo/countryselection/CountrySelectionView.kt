package eu.kevin.demo.countryselection

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import eu.kevin.common.architecture.BaseView
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.entities.LoadingState
import eu.kevin.common.entities.isLoading
import eu.kevin.common.extensions.applySystemInsetsPadding
import eu.kevin.common.extensions.fadeIn
import eu.kevin.common.extensions.fadeOut
import eu.kevin.common.helpers.ErrorHelper
import eu.kevin.common.helpers.SnackbarHelper
import eu.kevin.demo.countryselection.adapters.CountryListAdapter
import eu.kevin.demo.countryselection.entities.Country
import eu.kevin.demo.countryselection.helpers.CountryHelper
import eu.kevin.demo.databinding.FragmentCountrySelectionBinding

internal class CountrySelectionView(context: Context) : BaseView<FragmentCountrySelectionBinding>(context), IView<CountrySelectionState> {

    override val binding = FragmentCountrySelectionBinding.inflate(LayoutInflater.from(context), this)

    var delegate: CountrySelectionViewDelegate? = null

    private val countriesAdapter = CountryListAdapter {
        delegate?.onCountryClicked(it)
    }.also {
        it.context = context
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