package jacksondeng.revoluttest.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jacksondeng.revoluttest.R
import jacksondeng.revoluttest.databinding.ItemCurrencyBinding
import jacksondeng.revoluttest.model.entity.Currency


class RatesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemCurrencyBinding = DataBindingUtil.inflate(
            layoutInflater, viewType, parent, false
        )
        return CurrencyViewHolder(binding)
    }


    override fun getItemViewType(position: Int): Int {
        return R.layout.item_currency
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CurrencyViewHolder).bind(differ.currentList[position])
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem.rate == newItem.rate
        }

        override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem.name == newItem.name && oldItem.rate != newItem.rate
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun clear() = differ.submitList(null)

    fun submitList(list: List<Currency>?) = differ.submitList((list))

}
