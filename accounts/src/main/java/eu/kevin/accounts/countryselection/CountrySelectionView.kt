package eu.kevin.accounts.countryselection

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import eu.kevin.accounts.countryselection.adapters.CountryListAdapter
import eu.kevin.accounts.databinding.FragmentCountrySelectionBinding
import eu.kevin.core.architecture.BaseView
import eu.kevin.core.architecture.interfaces.IView
import eu.kevin.core.entities.LoadingState
import eu.kevin.core.entities.isLoading
import eu.kevin.core.extensions.fadeIn
import eu.kevin.core.extensions.fadeOut

internal class CountrySelectionView(context: Context) : BaseView<FragmentCountrySelectionBinding>(context), IView<CountrySelectionState> {

    override val binding = FragmentCountrySelectionBinding.inflate(LayoutInflater.from(context), this)

    var delegate: CountrySelectionViewDelegate? = null

    private val countriesAdapter = CountryListAdapter {
        delegate?.onCountryClicked(it)
    }

    init {
        with(binding.countriesRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = countriesAdapter
        }
    }

    override fun render(state: CountrySelectionState) = with(binding) {
        when (state.loadingState) {
            is LoadingState.FailureWithMessage -> {
                Toast.makeText(context, state.loadingState.message, Toast.LENGTH_SHORT).show()
            }
            is LoadingState.Loading -> showLoading(state.loadingState.isLoading())
            is LoadingState.Failure -> {
                progressView.fadeOut()
                when (state.loadingState.error) {
                    else -> {
                        Toast.makeText(
                            context,
                            state.loadingState.error.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
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
}