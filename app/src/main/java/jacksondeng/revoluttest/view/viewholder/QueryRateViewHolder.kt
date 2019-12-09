package jacksondeng.revoluttest.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import jacksondeng.revoluttest.databinding.ItemQueryRateBinding
import jacksondeng.revoluttest.model.entity.CurrencyModel
import java.util.*

class QueryRateViewHolder(private val binding: ItemQueryRateBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(currency: CurrencyModel) {
        binding.obj = currency
        binding.executePendingBindings()
    }
}
