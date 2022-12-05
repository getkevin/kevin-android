package eu.kevin.demo.screens.chooseaccount

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import eu.kevin.common.architecture.BaseView
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.extensions.applySystemInsetsPadding
import eu.kevin.demo.databinding.KevinFragmentChooseAccountBinding
import eu.kevin.demo.screens.chooseaccount.adapters.AccountsListAdapter

internal class ChooseAccountView(context: Context) :
    BaseView<KevinFragmentChooseAccountBinding>(context),
    IView<ChooseAccountState> {

    override var binding: KevinFragmentChooseAccountBinding? = KevinFragmentChooseAccountBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    var callback: ChooseAccountViewCallback? = null

    private val accountsListAdapter = AccountsListAdapter {
        callback?.onAccountChosen(it)
    }

    override fun render(state: ChooseAccountState) {
        accountsListAdapter.updateItems(state.accounts)
    }

    init {
        with(requireBinding().accountsRecyclerView) {
            applySystemInsetsPadding(bottom = true)
            layoutManager = LinearLayoutManager(context)
            adapter = accountsListAdapter
        }
    }
}