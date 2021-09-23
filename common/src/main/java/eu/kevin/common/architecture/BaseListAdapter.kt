package eu.kevin.common.architecture

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseListAdapter<T, B : ViewBinding>(
    open var items: List<T>
) : RecyclerView.Adapter<BaseListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    abstract fun onBindViewHolder(binding: B, item: T, position: Int)

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBindViewHolder(holder.binding as B, items[position], position)
    }

    override fun getItemCount() = items.size

    open fun updateItems(items: List<T>) {
        if (this.items == items) return
        this.items = items
        notifyDataSetChanged()
    }
}