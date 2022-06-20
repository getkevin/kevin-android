package eu.kevin.demo.screens.accountlinking

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.demo.R
import eu.kevin.demo.databinding.KevinFragmentLinkAccountBinding
import eu.kevin.demo.screens.accountlinking.adapters.LinkedAccountsListAdapter
import eu.kevin.demo.views.DividerItemDecoration

internal class AccountLinkingView(context: Context) : FrameLayout(context), IView<AccountLinkingState> {

    var callback: AccountLinkingViewCallback? = null

    private val accountsListAdapter = LinkedAccountsListAdapter {
        callback?.onOpenMenuClick(it)
    }

    private val binding = KevinFragmentLinkAccountBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        with(binding) {
            proceedButton.setOnClickListener {
                callback?.onLinkAccountClick()
            }
            linkNewButton.setOnClickListener {
                callback?.onLinkAccountClick()
            }
            with(binding.linkedAccountsList) {
                layoutManager = LinearLayoutManager(context)
                adapter = accountsListAdapter
                addItemDecoration(
                    DividerItemDecoration(ContextCompat.getDrawable(context, R.drawable.kevin_divider)!!)
                )
            }
        }
    }

    override fun render(state: AccountLinkingState) {
        with(binding) {
            progressView.isVisible = state.isLoading
            emptyStateContainer.isVisible = state.linkedAccounts.isEmpty()
            accountsStateGroup.isVisible = state.linkedAccounts.isNotEmpty()

            accountsListAdapter.updateItems(state.linkedAccounts)
        }
    }
}