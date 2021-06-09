package eu.kevin.accounts.bankselection

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import eu.kevin.accounts.R
import eu.kevin.accounts.bankselection.adapters.BankListAdapter
import eu.kevin.core.views.GridListItemDecoration
import eu.kevin.accounts.bankselection.exceptions.BankNotSelectedException
import eu.kevin.accounts.countryselection.helpers.CountryHelper
import eu.kevin.accounts.databinding.FragmentBankSelectionBinding
import eu.kevin.core.architecture.BaseView
import eu.kevin.core.architecture.interfaces.IView
import eu.kevin.core.entities.LoadingState
import eu.kevin.core.extensions.fadeIn
import eu.kevin.core.extensions.fadeOut
import eu.kevin.core.extensions.getColorFromAttr
import eu.kevin.core.helpers.ErrorHelper
import eu.kevin.core.helpers.SnackbarHelper

internal class BankSelectionView(context: Context) : BaseView<FragmentBankSelectionBinding>(context),
    IView<BankSelectionState> {

    override val binding = FragmentBankSelectionBinding.inflate(LayoutInflater.from(context), this)

    var delegate: BankSelectionViewDelegate? = null

    private val banksAdapter = BankListAdapter {
        delegate?.onBankClicked(it)
    }

    init {
        with(binding) {
            root.setBackgroundColor(context.getColorFromAttr(R.attr.kevinPrimaryBackgroundColor))
            with(banksRecyclerView) {
                addItemDecoration(GridListItemDecoration())
                layoutManager = GridLayoutManager(context, 2)
                adapter = banksAdapter
            }
            continueButton.setOnClickListener {
                delegate?.onContinueClicked()
            }
            actionBar.setNavigationOnClickListener {
                delegate?.onBackClicked()
            }
            countrySelectionView.setOnClickListener {
                delegate?.onSelectCountryClicked()
            }
        }
    }

    override fun render(state: BankSelectionState) = with(binding) {
        banksAdapter.updateItems(state.supportedBanks)
        countrySelectionView.image = CountryHelper.getCountryFlagDrawable(context, state.selectedCountry)
        countrySelectionView.title = CountryHelper.getCountryName(context, state.selectedCountry)
        when (state.loadingState) {
            is LoadingState.Loading -> startLoading(state.loadingState.isLoading)
            is LoadingState.FailureWithMessage -> showErrorMessage(state.loadingState.message)
            is LoadingState.Failure -> showFailure(state.loadingState.error)
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