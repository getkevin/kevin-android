package eu.kevin.accounts.bankselection.adapters

import androidx.recyclerview.widget.DiffUtil
import eu.kevin.accounts.bankselection.entities.Bank

internal class BankListDiffCallback(private val oldList: List<Bank>, private val newList: List<Bank>): DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].bankId == newList[newItemPosition].bankId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}