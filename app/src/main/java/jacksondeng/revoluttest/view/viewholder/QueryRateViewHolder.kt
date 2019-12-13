package jacksondeng.revoluttest.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import jacksondeng.revoluttest.databinding.ItemQueryRateBinding
import jacksondeng.revoluttest.model.entity.CurrencyModel
import jacksondeng.revoluttest.util.CurrencyFormatInputFilter

class QueryRateViewHolder(val binding: ItemQueryRateBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(currency: CurrencyModel) {
        binding.obj = currency
        binding.executePendingBindings()
        binding.queryAmount.filters = arrayOf(CurrencyFormatInputFilter())
    }
}
