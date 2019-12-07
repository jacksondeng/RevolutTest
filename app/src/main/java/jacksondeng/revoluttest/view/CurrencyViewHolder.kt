package jacksondeng.revoluttest.view

import androidx.recyclerview.widget.RecyclerView
import jacksondeng.revoluttest.databinding.ItemCurrencyBinding
import jacksondeng.revoluttest.model.entity.Currency

class CurrencyViewHolder(private val binding: ItemCurrencyBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(currency: Currency) {
        binding.currency = currency
        binding.executePendingBindings()
    }
}