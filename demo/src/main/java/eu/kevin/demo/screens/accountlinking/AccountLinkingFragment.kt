package eu.kevin.demo.screens.accountlinking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import eu.kevin.accounts.accountsession.AccountSessionContract
import eu.kevin.accounts.accountsession.entities.AccountSessionConfiguration
import eu.kevin.common.extensions.setFragmentResultListener
import eu.kevin.core.enums.KevinCountry
import eu.kevin.demo.screens.accountactions.AccountActionsContract
import eu.kevin.demo.screens.countryselection.CountrySelectionContract
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AccountLinkingFragment : Fragment(), AccountLinkingViewCallback {

    private val viewModel: AccountLinkingViewModel by activityViewModels {
        AccountLinkingViewModel.Factory(
            requireActivity().applicationContext,
            this
        )
    }

    private val linkAccount = registerForActivityResult(AccountSessionContract()) { result ->
        viewModel.onAccountLinkingResult(result)
    }

    private lateinit var contentView: AccountLinkingView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        observeChanges()
        listenForAccountActionSelectedResult()
        return AccountLinkingView(inflater.context).also {
            it.callback = this
            contentView = it
        }
    }

    private fun listenForAccountActionSelectedResult() {
        parentFragmentManager.setFragmentResultListener(AccountActionsContract, this) {
            viewModel.onAccountActionSelected(it)
        }
    }

    private fun observeChanges() {
        lifecycleScope.launchWhenStarted {
            viewModel.viewState.onEach { viewState ->
                contentView.update(viewState)
            }.launchIn(this)

            viewModel.viewAction.onEach { action ->
                when (action) {
                    is AccountLinkingAction.OpenAccountLinkingSession -> {
                        openAccountLinkingSession(action.state)
                    }
                }
            }.launchIn(this)
        }
    }

    private fun openAccountLinkingSession(state: String) {
        val config = AccountSessionConfiguration.Builder(state)
            .setPreselectedCountry(KevinCountry.LITHUANIA)
            .setDisableCountrySelection(false)
            .build()
        linkAccount.launch(config)
    }

    override fun onLinkAccountClick() {
        viewModel.startAccountLinking()
    }

    override fun onOpenMenuClick(id: Long) {
        viewModel.openMenu(id)
    }
}