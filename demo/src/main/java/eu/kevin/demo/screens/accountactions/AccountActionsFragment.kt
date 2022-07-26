package eu.kevin.demo.screens.accountactions

import android.content.Context
import androidx.fragment.app.viewModels
import eu.kevin.common.architecture.BaseModalFragment
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.demo.screens.accountactions.AccountActionsIntent.HandleRemoveAccount

internal class AccountActionsFragment :
    BaseModalFragment<AccountActionsState, AccountActionsIntent, AccountActionsViewModel>(), AccountActionsCallback {

    var configuration: AccountActionsFragmentConfiguration? by savedState()

    override val viewModel by viewModels<AccountActionsViewModel> {
        AccountActionsViewModel.Factory(
            requireContext(),
            this
        )
    }

    override fun onAttached() {
        viewModel.initialise(configuration!!)
    }

    override fun onCreateView(context: Context): IView<AccountActionsState> {
        return AccountActionsView(context).also {
            it.callback = this
        }
    }

    override fun onRemoveAccountClick() {
        viewModel.intents.trySend(HandleRemoveAccount(configuration!!.id))
        dismiss()
    }
}