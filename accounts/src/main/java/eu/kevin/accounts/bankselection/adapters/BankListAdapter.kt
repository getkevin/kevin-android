package eu.kevin.accounts.bankselection.adapters

import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import coil.load
import eu.kevin.accounts.bankselection.entities.BankListItem
import eu.kevin.accounts.databinding.KevinViewBankListItemBinding
import eu.kevin.common.R
import eu.kevin.common.architecture.BaseListAdapter
import eu.kevin.common.extensions.getBooleanFromAttr
import eu.kevin.common.extensions.setDebounceClickListener

internal class BankListAdapter(
    override var items: List<BankListItem> = emptyList(),
    private val onBankClicked: (String) -> Unit
) : BaseListAdapter<BankListItem, KevinViewBankListItemBinding>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(KevinViewBankListItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(binding: KevinViewBankListItemBinding, item: BankListItem, position: Int) {
        with(binding) {
            root.setDebounceClickListener {
                onBankClicked.invoke(item.bankId)
            }
            root.contentDescription = item.title
            bankTitleView.text = item.title
            loadBankImage(binding, item.logoUrl)
            root.isSelected = item.isSelected
        }
    }

    override fun updateItems(items: List<BankListItem>) {
        val diffResult = DiffUtil.calculateDiff(BankListDiffCallback(this.items, items))
        this.items = items
        diffResult.dispatchUpdatesTo(this)
    }

    private fun loadBankImage(binding: KevinViewBankListItemBinding, logoUrl: String) {
        val url = if (binding.root.context.getBooleanFromAttr(R.attr.kevinUseLightBankIcons)) {
            try {
                val urlParts = logoUrl.split("images/")
                "${urlParts[0]}images/white/${urlParts[1]}"
            } catch (e: Exception) {
                logoUrl
            }
        } else {
            logoUrl
        }
        with(binding) {
            bankImageView.load(url) {
                listener(
                    onStart = {
                        bankTitleView.visibility = VISIBLE
                        bankImageView.visibility = INVISIBLE
                    },
                    onSuccess = { _, _ ->
                        bankTitleView.visibility = INVISIBLE
                        bankImageView.visibility = VISIBLE
                    },
                    onError = { _, _ ->
                        bankTitleView.visibility = VISIBLE
                        bankImageView.visibility = INVISIBLE
                    }
                )
            }
        }
    }
}