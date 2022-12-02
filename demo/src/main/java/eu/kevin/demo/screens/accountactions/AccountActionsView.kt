package eu.kevin.demo.screens.accountactions

import android.content.Context
import android.view.LayoutInflater
import eu.kevin.common.architecture.BaseView
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.extensions.applySystemInsetsPadding
import eu.kevin.demo.databinding.KevinFragmentAccountActionsBinding

internal class AccountActionsView(context: Context) :
    BaseView<KevinFragmentAccountActionsBinding>(context),
    IView<AccountActionsState> {

    override var binding: KevinFragmentAccountActionsBinding? = KevinFragmentAccountActionsBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    var callback: AccountActionsCallback? = null

    init {
        requireBinding().root.applySystemInsetsPadding(bottom = true)
    }

    override fun render(state: AccountActionsState) {
        with(requireBinding()) {
            titleTextView.text = state.bankName
            removeAccountButton.setOnClickListener {
                callback?.onRemoveAccountClick()
            }
        }
    }
}