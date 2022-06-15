package eu.kevin.demo.screens.payment.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import eu.kevin.demo.databinding.KevinItemCreditorBinding
import eu.kevin.demo.screens.payment.entities.CreditorListItem

internal class CreditorsAdapter(
    private var creditorsList: List<CreditorListItem> = emptyList(),
    private val onItemSelected: (CreditorListItem) -> Unit
) : RecyclerView.Adapter<CreditorsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: KevinItemCreditorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            KevinItemCreditorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.root.isSelected = creditorsList[position].isSelected
        holder.binding.creditorLogoImageView.load(creditorsList[position].logo)
        holder.binding.root.setOnClickListener {
            onItemSelected(creditorsList[position])
        }
    }

    override fun getItemCount(): Int {
        return creditorsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(list: List<CreditorListItem>) {
        creditorsList = list
        notifyDataSetChanged()
    }
}