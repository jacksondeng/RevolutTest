package jacksondeng.revoluttest.view.viewholder

import android.content.SharedPreferences
import androidx.recyclerview.widget.RecyclerView
import jacksondeng.revoluttest.databinding.ItemExchangeRateBinding
import jacksondeng.revoluttest.model.entity.CurrencyModel
import jacksondeng.revoluttest.util.updateSelectedBase
import jacksondeng.revoluttest.view.adapter.InterActionListener

class ExchangeRateViewHolder(
    private val binding: ItemExchangeRateBinding,
    private val interActionListener: InterActionListener,
    private val sharePref: SharedPreferences
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(currency: CurrencyModel) {
        binding.obj = currency
        binding.executePendingBindings()
        binding.root.setOnClickListener {
            sharePref.updateSelectedBase(currency.currency.currencyCode)
            interActionListener.onItemClicked(adapterPosition)
        }
    }
}