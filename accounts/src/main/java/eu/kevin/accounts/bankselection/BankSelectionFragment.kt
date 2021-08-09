package eu.kevin.accounts.bankselection

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.viewModels
import eu.kevin.accounts.bankselection.BankSelectionIntent.*
import eu.kevin.accounts.countryselection.CountrySelectionFragment
import eu.kevin.accounts.bankselection.entities.Bank
import eu.kevin.common.architecture.BaseFragment
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.extensions.setFragmentResultListener
import eu.kevin.common.fragment.FragmentResultContract

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
        viewModel.intents.trySend(Initialize(configuration!!))
        parentFragmentManager.setFragmentResultListener(CountrySelectionFragment.Contract, this) {
            viewModel.intents.trySend(HandleCountrySelected(it, configuration!!))
        }
    }

    override fun onBackPressed(): Boolean {
        viewModel.intents.trySend(HandleBackClicked)
        return true
    }

    // BankSelectionViewDelegate

    override fun onBackClicked() {
        viewModel.intents.trySend(HandleBackClicked)
    }

    override fun onBankClicked(bankId: String) {
        viewModel.intents.trySend(HandleBankSelection(bankId))
    }

    override fun onSelectCountryClicked() {
        viewModel.intents.trySend(HandleCountrySelectionClick(configuration!!))
    }

    override fun onContinueClicked() {
        viewModel.intents.trySend(HandleContinueClicked)
    }

    object Contract: FragmentResultContract<Bank>() {
        override val requestKey = "bank_selection_request_key"
        override val resultKey = "bank_selection_result_key"
        override fun parseResult(data: Bundle): Bank {
            return data.getParcelable(resultKey)!!
        }
    }
}