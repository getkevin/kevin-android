package eu.kevin.accounts.countryselection.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import eu.kevin.accounts.R
import eu.kevin.accounts.countryselection.entities.Country
import eu.kevin.accounts.countryselection.helpers.CountryHelper
import eu.kevin.accounts.databinding.ViewCountryListItemBinding
import eu.kevin.common.architecture.BaseListAdapter
import eu.kevin.common.extensions.getDrawableCompat
import eu.kevin.common.extensions.setDebounceClickListener

internal class CountryListAdapter(
    override var items: List<Country> = emptyList(),
    private val onCountryClicked: (String) -> Unit
) : BaseListAdapter<Country, ViewCountryListItemBinding>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ViewCountryListItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(binding: ViewCountryListItemBinding, item: Country, position: Int) {
        val context = binding.root.context
        val background = when (position) {
            0 -> context.getDrawableCompat(R.drawable.country_list_item_background_top)
            items.size - 1 -> context.getDrawableCompat(R.drawable.country_list_item_background_bottom)
            else -> context.getDrawableCompat(R.drawable.country_list_item_background_middle)
        }
        val foreground = when (position) {
            0 -> context.getDrawableCompat(R.drawable.country_list_item_ripple_top)
            items.size - 1 -> context.getDrawableCompat(R.drawable.country_list_item_ripple_bottom)
            else -> context.getDrawableCompat(R.drawable.country_list_item_ripple_middle)
        }
        with(binding) {
            root.setDebounceClickListener {
                onCountryClicked.invoke(item.iso)
            }
            root.isSelected = item.isSelected
            root.background = background
            root.foreground = foreground
            countryTextView.text = item.title
            countryFlagImageView.setImageDrawable(CountryHelper.getCountryFlagDrawable(context, item.iso))
        }
    }

    override fun updateItems(items: List<Country>) {
        val diffResult = DiffUtil.calculateDiff(CountryListDiffCallback(this.items, items))
        this.items = items
        diffResult.dispatchUpdatesTo(this)
    }
}