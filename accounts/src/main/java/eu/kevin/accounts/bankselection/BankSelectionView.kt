package eu.kevin.accounts.bankselection

import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.TransitionManager
import eu.kevin.accounts.R
import eu.kevin.accounts.bankselection.adapters.BankListAdapter
import eu.kevin.accounts.bankselection.exceptions.BankNotSelectedException
import eu.kevin.accounts.countryselection.helpers.CountryHelper
import eu.kevin.accounts.databinding.KevinFragmentBankSelectionBinding
import eu.kevin.common.architecture.BaseView
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.entities.LoadingState
import eu.kevin.common.extensions.applySystemInsetsMargin
import eu.kevin.common.extensions.applySystemInsetsPadding
import eu.kevin.common.extensions.fadeIn
import eu.kevin.common.extensions.fadeOut
import eu.kevin.common.extensions.getColorFromAttr
import eu.kevin.common.extensions.setDebounceClickListener
import eu.kevin.common.helpers.ErrorHelper
import eu.kevin.common.helpers.SnackbarHelper
import eu.kevin.common.helpers.SpannableStringHelper
import eu.kevin.common.helpers.SpannableStringLink
import eu.kevin.common.views.GridListItemDecoration

internal class BankSelectionView(context: Context) :
    BaseView<KevinFragmentBankSelectionBinding>(context),
    IView<BankSelectionState> {

    override val binding = KevinFragmentBankSelectionBinding.inflate(LayoutInflater.from(context), this)

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
            continueButton.setDebounceClickListener {
                delegate?.onContinueClicked()
            }
            continueButton.applySystemInsetsMargin(bottom = true)
            actionBar.setNavigationOnClickListener {
                delegate?.onBackClicked()
            }
            actionBar.applySystemInsetsPadding(top = true)
            actionBar.setNavigationContentDescription(R.string.kevin_navigate_back_content_description)
            countrySelectionView.setDebounceClickListener {
                delegate?.onSelectCountryClicked()
            }

            termsText.text = SpannableStringHelper.getSpannableWithLinks(
                context.getString(R.string.kevin_window_bank_selection_terms_and_conditions_text),
                context.getColorFromAttr(R.attr.colorPrimary),
                SpannableStringLink(context.getString(R.string.kevin_window_bank_selection_terms_clickable_text)) {
                    delegate?.onTermsAndConditionsClicked()
                },
                SpannableStringLink(context.getString(R.string.kevin_window_bank_selection_privacy_clickable_text)) {
                    delegate?.onPrivacyPolicyClicked()
                }
            )
            termsText.movementMethod = LinkMovementMethod()
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
            null -> startLoading(false)
        }
    }

    private fun showCountrySelection(show: Boolean) {
        val visibility = if (show) VISIBLE else GONE
        with(binding) {
            TransitionManager.beginDelayedTransition(this@BankSelectionView)
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
                showErrorMessage(context.getString(R.string.kevin_error_bank_not_selected))
            }
            else -> showErrorMessage(ErrorHelper.getMessage(context, error))
        }
    }

    private fun showErrorMessage(message: String) {
        binding.progressView.fadeOut()
        SnackbarHelper.showError(this, message)
    }
}