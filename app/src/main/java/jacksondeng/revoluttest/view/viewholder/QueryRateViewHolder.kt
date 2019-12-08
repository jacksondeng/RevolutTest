package jacksondeng.revoluttest.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import jacksondeng.revoluttest.databinding.ItemQueryRateBinding
import java.util.*

class QueryRateViewHolder(private val binding: ItemQueryRateBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind() {
        val currency = Currency.getInstance("GBP")
        binding.queryCountry.text = "GBP"
        binding.countryDisplayName.text = currency.displayName
    }
}
