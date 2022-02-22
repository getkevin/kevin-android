package eu.kevin.accounts.bankselection

import android.content.Context
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
            root.setBackgroundColor(context.getColorFromAttr(R.attr.kevinColorBackground))
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
        showCountrySelection(!state.isCountrySelectionDisabled)
        when (state.loadingState) {
            is LoadingState.Loading -> startLoading(state.loadingState.isLoading)
            is LoadingState.FailureWithMessage -> showErrorMessage(state.loadingState.message)
            is LoadingState.Failure -> showFailure(state.loadingState.error)
        }
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
                progressView.fadeIn()
            } else {
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
}