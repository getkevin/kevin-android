package eu.kevin.accounts.countryselection

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import eu.kevin.accounts.countryselection.adapters.CountryListAdapter
import eu.kevin.accounts.databinding.FragmentCountrySelectionBinding
import eu.kevin.core.architecture.BaseView
import eu.kevin.core.architecture.interfaces.IView
import eu.kevin.core.entities.LoadingState
import eu.kevin.core.entities.isLoading
import eu.kevin.core.extensions.applySystemInsetsPadding
import eu.kevin.core.extensions.fadeIn
import eu.kevin.core.extensions.fadeOut
import eu.kevin.core.helpers.ErrorHelper
import eu.kevin.core.helpers.SnackbarHelper

internal class CountrySelectionView(context: Context) : BaseView<FragmentCountrySelectionBinding>(context), IView<CountrySelectionState> {

    override val binding = FragmentCountrySelectionBinding.inflate(LayoutInflater.from(context), this)

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
        }
        countriesAdapter.updateItems(state.supportedCountries)
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