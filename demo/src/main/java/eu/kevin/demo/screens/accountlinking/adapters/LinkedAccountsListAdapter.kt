package eu.kevin.demo.screens.accountlinking.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import coil.load
import eu.kevin.common.extensions.getDrawableCompat
import eu.kevin.demo.R
import eu.kevin.demo.data.database.entities.LinkedAccount
import eu.kevin.demo.databinding.KevinItemLinkedAccountBinding
import eu.kevin.demo.screens.countryselection.adapters.BaseListAdapter

internal class LinkedAccountsListAdapter(
    override var items: List<LinkedAccount> = emptyList(),
    private val onMoreClick: (Long) -> Unit
) : BaseListAdapter<LinkedAccount, KevinItemLinkedAccountBinding>(items) {

    override fun onBindingRequested(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): KevinItemLinkedAccountBinding {
        return KevinItemLinkedAccountBinding.inflate(inflater, parent, false)
    }

    override fun onBindViewHolder(binding: KevinItemLinkedAccountBinding, item: LinkedAccount, position: Int) {
        val background = when {
            items.size == 1 -> context.getDrawableCompat(R.drawable.kevin_linked_accounts_list_item_background_single)
            position == 0 -> context.getDrawableCompat(R.drawable.kevin_linked_accounts_list_item_background_top)
            position == items.size - 1 -> context.getDrawableCompat(
                R.drawable.kevin_linked_accounts_list_item_background_bottom
            )
            else -> context.getDrawableCompat(R.drawable.kevin_linked_accounts_list_item_background_middle)
        }
        with(binding) {
            root.background = background
            bankIconImage.load(item.logoUrl)
            bankNameTextView.text = item.bankName
            moreMenuImageButton.setOnClickListener {
                onMoreClick(item.id)
            }
        }
    }

    override fun updateItems(items: List<LinkedAccount>) {
        this.items = items
        notifyDataSetChanged()
    }
}