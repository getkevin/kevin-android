package eu.kevin.accounts.bankselection.adapters

import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import coil.ImageLoader
import coil.request.ImageRequest
import eu.kevin.accounts.bankselection.entities.BankListItem
import eu.kevin.accounts.databinding.ViewBankListItemBinding
import eu.kevin.core.architecture.BaseListAdapter

internal class BankListAdapter(
    override var items: List<BankListItem> = emptyList(),
    private val onBankClicked: (String) -> Unit
) : BaseListAdapter<BankListItem, ViewBankListItemBinding>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ViewBankListItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(binding: ViewBankListItemBinding, item: BankListItem, position: Int) {
        with(binding) {
            root.setOnClickListener {
                onBankClicked.invoke(item.bankId)
            }
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

    private fun loadBankImage(binding: ViewBankListItemBinding, logoUrl: String) {
        with(binding) {
            val imageRequest = ImageRequest.Builder(root.context)
                .data(logoUrl)
                .target(
                    onSuccess = {
                        bankImageView.setImageDrawable(it)
                        bankTitleView.visibility = INVISIBLE
                        bankImageView.visibility = VISIBLE
                    },
                    onError = {
                        bankTitleView.visibility = VISIBLE
                        bankImageView.visibility = INVISIBLE
                    }
                )
                .build()
            ImageLoader.invoke(root.context).enqueue(imageRequest)
        }
    }
}