package eu.kevin.accounts.bankselection

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.viewModels
import eu.kevin.accounts.bankselection.BankSelectionIntent.*
import eu.kevin.accounts.countryselection.CountrySelectionFragment
import eu.kevin.core.architecture.BaseFragment
import eu.kevin.core.architecture.interfaces.IView
import eu.kevin.core.extensions.setFragmentResultListener
import eu.kevin.core.fragment.FragmentResultContract

class BankSelectionFragment : BaseFragment<BankSelectionState, BankSelectionIntent, BankSelectionViewModel>(),
    BankSelectionViewDelegate {

    var configuration: BankSelectionFragmentConfiguration? by savedState()

    override val viewModel: BankSelectionViewModel by viewModels {
        BankSelectionViewModel.Factory(this)
    }

    override fun onCreateView(context: Context): IView<BankSelectionState> {
        return BankSelectionView(context).also {
            it.delegate = this
        }
    }

    override fun onAttached() {
        viewModel.intents.offer(Initialize(configuration!!))
        parentFragmentManager.setFragmentResultListener(CountrySelectionFragment.Contract, this) {
            viewModel.intents.offer(HandleCountrySelected(it, configuration!!))
        }
    }

    override fun onBackPressed(): Boolean {
        viewModel.intents.offer(HandleBackClicked)
        return true
    }

    // BankSelectionViewDelegate

    override fun onBackClicked() {
        viewModel.intents.offer(HandleBackClicked)
    }

    override fun onBankClicked(bankId: String) {
        viewModel.intents.offer(HandleBankSelection(bankId))
    }

    override fun onSelectCountryClicked() {
        viewModel.intents.offer(HandleCountrySelectionClick(configuration!!))
    }

    override fun onContinueClicked() {
        viewModel.intents.offer(HandleContinueClicked)
    }

    object Contract: FragmentResultContract<String>() {
        override val requestKey = "bank_selection_request_key"
        override val resultKey = "bank_selection_result_key"
        override fun parseResult(data: Bundle): String {
            return data.getString(resultKey) ?: ""
        }
    }
}