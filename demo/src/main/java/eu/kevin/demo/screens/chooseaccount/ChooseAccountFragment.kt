package eu.kevin.demo.screens.chooseaccount

import android.content.Context
import androidx.fragment.app.viewModels
import eu.kevin.common.architecture.BaseModalFragment
import eu.kevin.common.architecture.interfaces.View
import eu.kevin.demo.screens.chooseaccount.ChooseAccountIntent.OnAccountChosen

internal class ChooseAccountFragment :
    BaseModalFragment<ChooseAccountState, ChooseAccountIntent, ChooseAccountViewModel>(), ChooseAccountViewCallback {

    override val viewModel by viewModels<ChooseAccountViewModel> {
        ChooseAccountViewModel.Factory(
            requireContext(),
            this
        )
    }

    override fun onCreateView(context: Context): View<ChooseAccountState> {
        return ChooseAccountView(context).also {
            it.callback = this
        }
    }

    override fun onAccountChosen(id: Long) {
        dismiss()
        viewModel.intents.trySend(OnAccountChosen(id))
    }
}