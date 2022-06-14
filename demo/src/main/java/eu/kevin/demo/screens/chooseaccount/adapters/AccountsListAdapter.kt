package eu.kevin.demo.screens.chooseaccount.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import coil.load
import eu.kevin.common.extensions.getDrawableCompat
import eu.kevin.common.extensions.setDebounceClickListener
import eu.kevin.demo.R
import eu.kevin.demo.data.database.entities.LinkedAccount
import eu.kevin.demo.databinding.KevinItemChooseLinkedAccountBinding
import eu.kevin.demo.screens.countryselection.adapters.BaseListAdapter

class AccountsListAdapter(
    override var items: List<LinkedAccount> = emptyList(),
    private val onItemClick: (Long) -> Unit
) : BaseListAdapter<LinkedAccount, KevinItemChooseLinkedAccountBinding>(items) {

    override fun onBindingRequested(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): KevinItemChooseLinkedAccountBinding {
        return KevinItemChooseLinkedAccountBinding.inflate(inflater, parent, false)
    }

    override fun onBindViewHolder(binding: KevinItemChooseLinkedAccountBinding, item: LinkedAccount, position: Int) {
        val background = when {
            items.size == 1 -> context.getDrawableCompat(R.drawable.kevin_linked_accounts_list_item_background_single)
            position == 0 -> context.getDrawableCompat(R.drawable.kevin_linked_accounts_list_item_background_top)
            position == items.size - 1 -> context.getDrawableCompat(R.drawable.kevin_linked_accounts_list_item_background_bottom)
            else -> context.getDrawableCompat(R.drawable.kevin_linked_accounts_list_item_background_middle)
        }
        val foreground = when {
            items.size == 1 -> context.getDrawableCompat(R.drawable.kevin_country_selection_ripple_single)
            position == 0 -> context.getDrawableCompat(R.drawable.kevin_country_selection_ripple_top)
            position == items.size - 1 -> context.getDrawableCompat(R.drawable.kevin_country_selection_ripple_bottom)
            else -> context.getDrawableCompat(R.drawable.kevin_country_selection_ripple_middle)
        }
        with(binding) {
            root.setDebounceClickListener {
                onItemClick(item.id)
            }
            root.background = background
            root.foreground = foreground
            bankNameTextView.text = item.bankName
            bankIconImage.load(item.logoUrl)
        }
    }

    override fun updateItems(items: List<LinkedAccount>) {
        this.items = items
        notifyDataSetChanged()
    }
}