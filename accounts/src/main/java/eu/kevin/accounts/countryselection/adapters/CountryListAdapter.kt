package eu.kevin.accounts.countryselection.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import eu.kevin.accounts.R
import eu.kevin.accounts.countryselection.entities.Country
import eu.kevin.accounts.countryselection.helpers.CountryHelper
import eu.kevin.accounts.databinding.ViewCountryListItemBinding
import eu.kevin.common.architecture.BaseListAdapter
import eu.kevin.common.extensions.getBooleanFromAttr
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
        with(binding) {
            root.setDebounceClickListener {
                onCountryClicked.invoke(item.iso)
            }
            root.isSelected = item.isSelected
            countryTextView.text = item.title
            countryFlagImageView.setImageDrawable(CountryHelper.getCountryFlagDrawable(context, item.iso))

            if (!context.getBooleanFromAttr(R.attr.kevinOverrideCountryBackground)) {
                val background = when (position) {
                    0 -> context.getDrawableCompat(R.drawable.country_list_item_background_top)
                    items.size - 1 -> context.getDrawableCompat(R.drawable.country_list_item_background_bottom)
                    else -> context.getDrawableCompat(R.drawable.country_list_item_background_middle)
                }
                root.background = background
            }

            val alpha: Float
            if (!item.isActive) {
                binding.countryNotAvailableTextView.visibility = View.VISIBLE
                alpha = 0.4f
            } else {
                binding.countryNotAvailableTextView.visibility = View.GONE
                alpha = 1f
            }
            root.background.alpha = (255 * alpha).toInt()
            binding.countryFlagImageView.alpha = alpha
            binding.countryTextView.alpha = alpha
            binding.countryNotAvailableTextView.alpha = alpha
        }
    }

    override fun updateItems(items: List<Country>) {
        val diffResult = DiffUtil.calculateDiff(CountryListDiffCallback(this.items, items))
        this.items = items
        diffResult.dispatchUpdatesTo(this)
    }
}