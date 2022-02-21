package eu.kevin.demo.countryselection.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import eu.kevin.common.extensions.getColorFromAttr
import eu.kevin.common.extensions.setDebounceClickListener
import eu.kevin.demo.R
import eu.kevin.demo.countryselection.entities.Country
import eu.kevin.demo.countryselection.helpers.CountryHelper
import eu.kevin.demo.databinding.ItemCountryListBinding

internal class CountryListAdapter(
    override var items: List<Country> = emptyList(),
    private val onCountryClicked: (String) -> Unit
) : BaseListAdapter<Country, ItemCountryListBinding>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemCountryListBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(binding: ItemCountryListBinding, item: Country, position: Int) {
        with(binding) {
            root.setDebounceClickListener {
                onCountryClicked.invoke(item.iso)
            }
            root.setBackgroundColor(
                if (item.isSelected) context.getColorFromAttr(R.attr.selectedOnSecondaryColor) else 0
            )
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