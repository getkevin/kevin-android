package eu.kevin.accounts.bankselection

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.TransitionManager
import eu.kevin.accounts.R
import eu.kevin.accounts.bankselection.adapters.BankListAdapter
import eu.kevin.accounts.bankselection.exceptions.BankNotSelectedException
import eu.kevin.accounts.countryselection.helpers.CountryHelper
import eu.kevin.accounts.databinding.FragmentBankSelectionBinding
import eu.kevin.common.architecture.BaseView
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.entities.LoadingState
import eu.kevin.common.entities.isLoading
import eu.kevin.common.extensions.*
import eu.kevin.common.helpers.ErrorHelper
import eu.kevin.common.helpers.SnackbarHelper
import eu.kevin.common.views.GridListItemDecoration

internal class BankSelectionView(context: Context) : BaseView<FragmentBankSelectionBinding>(context),
    IView<BankSelectionState> {

    override val binding = FragmentBankSelectionBinding.inflate(LayoutInflater.from(context), this)

    var delegate: BankSelectionViewDelegate? = null

    private val banksAdapter = BankListAdapter {
        delegate?.onBankClicked(it)
    }

    init {
        with(binding) {
            with(banksRecyclerView) {
                addItemDecoration(GridListItemDecoration())
                layoutManager = GridLayoutManager(context, 2)
                adapter = banksAdapter
            }
            scrollView.applySystemInsetsPadding(bottom = true)
            continueButton.setDebounceClickListener {
                delegate?.onContinueClicked()
            }
            continueButton.applySystemInsetsMargin(bottom = true)
            actionBar.setNavigationOnClickListener {
                delegate?.onBackClicked()
            }
            actionBar.applySystemInsetsPadding(top = true)
            actionBar.setNavigationContentDescription(R.string.navigate_back_content_description)
            countrySelectionView.setDebounceClickListener {
                delegate?.onSelectCountryClicked()
            }
        }
    }

    override fun render(state: BankSelectionState) = with(binding) {
        banksAdapter.updateItems(state.bankListItems)
        countrySelectionView.image = CountryHelper.getCountryFlagDrawable(context, state.selectedCountry)
        countrySelectionView.title = CountryHelper.getCountryName(context, state.selectedCountry)
        emptyStateTitle.text = context.getString(R.string.window_bank_selection_empty_state_title).format(CountryHelper.getCountryName(context, state.selectedCountry))
        emptyStateSubtitle.text = context.getString(R.string.window_bank_selection_empty_state_subtitle).format(CountryHelper.getCountryName(context, state.selectedCountry))
        showCountrySelection(!state.isCountrySelectionDisabled)
        when (state.loadingState) {
            is LoadingState.Loading -> startLoading(state.loadingState.isLoading)
            is LoadingState.FailureWithMessage -> showErrorMessage(state.loadingState.message)
            is LoadingState.Failure -> showFailure(state.loadingState.error)
            null -> startLoading(false)
        }
        showEmptyState(!state.loadingState.isLoading() && state.bankListItems.isEmpty())
    }

    private fun showCountrySelection(show: Boolean) {
        val visibility = if (show) VISIBLE else GONE
        with(binding) {
            TransitionManager.beginDelayedTransition(contentRoot)
            countrySelectionLabel.visibility = visibility
            countrySelectionView.visibility = visibility
        }
    }

    private fun startLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                bankSelectionLabel.fadeOut()
                progressView.fadeIn()
            } else {
                bankSelectionLabel.fadeIn()
                progressView.fadeOut()
            }
        }
    }

    private fun showFailure(error: Throwable) {
        when (error) {
            is BankNotSelectedException -> {
                showErrorMessage(context.getString(R.string.error_bank_not_selected))
            }
            else -> showErrorMessage(ErrorHelper.getMessage(context, error))
        }
    }

    private fun showErrorMessage(message: String) {
        binding.progressView.fadeOut()
        SnackbarHelper.showError(this, message)
    }

    private fun showEmptyState(visible: Boolean) {
        if (visible) {
            binding.emptyStateGroup.fadeIn()
            binding.banksListGroup.visibility = GONE
        } else {
            binding.emptyStateGroup.visibility = GONE
            binding.banksListGroup.fadeIn()
        }
    }
}