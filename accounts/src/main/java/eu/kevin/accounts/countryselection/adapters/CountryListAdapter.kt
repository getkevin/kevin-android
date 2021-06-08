package eu.kevin.accounts.countryselection.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import eu.kevin.accounts.R
import eu.kevin.accounts.countryselection.entities.Country
import eu.kevin.accounts.countryselection.helpers.CountryHelper
import eu.kevin.accounts.databinding.ViewCountryListItemBinding
import eu.kevin.core.architecture.BaseListAdapter
import eu.kevin.core.extensions.getColorFromAttr

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
        with(binding) {
            root.setOnClickListener {
                onCountryClicked.invoke(item.iso)
            }
            root.setBackgroundColor(
                if (item.isSelected) context.getColorFromAttr(R.attr.kevinSelectedOnSecondaryColor) else 0
            )
            countryTextView.text = CountryHelper.getCountryName(context, item.iso)
            countryFlagImageView.setImageDrawable(CountryHelper.getCountryFlagDrawable(context, item.iso))
        }
    }

    override fun updateItems(items: List<Country>) {
        val diffResult = DiffUtil.calculateDiff(CountryListDiffCallback(this.items, items))
        this.items = items
        diffResult.dispatchUpdatesTo(this)
    }
}