package jacksondeng.revoluttest.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import jacksondeng.revoluttest.databinding.ItemExchangeRateBinding
import jacksondeng.revoluttest.model.entity.Currency

class ExchangeRateViewHolder(private val binding: ItemExchangeRateBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(currency: Currency) {
        binding.currency = currency
        binding.executePendingBindings()
    }
}