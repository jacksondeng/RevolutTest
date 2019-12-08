package jacksondeng.revoluttest.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jacksondeng.revoluttest.R
import jacksondeng.revoluttest.databinding.ItemExchangeRateBinding
import jacksondeng.revoluttest.databinding.ItemQueryRateBinding
import jacksondeng.revoluttest.model.entity.Currency
import jacksondeng.revoluttest.view.viewholder.ExchangeRateViewHolder
import jacksondeng.revoluttest.view.viewholder.QueryRateViewHolder

const val VIEW_TYPE_QUERY_RATE = 0
const val VIEW_TYPE_EXCHANGE_RATE = 1

class RatesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_QUERY_RATE -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding: ItemQueryRateBinding = DataBindingUtil.inflate(
                    layoutInflater, R.layout.item_query_rate, parent, false
                )
                return QueryRateViewHolder(binding)
            }

            VIEW_TYPE_EXCHANGE_RATE -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding: ItemExchangeRateBinding = DataBindingUtil.inflate(
                    layoutInflater, R.layout.item_exchange_rate, parent, false
                )
                return ExchangeRateViewHolder(binding)
            }

            else -> {
                throw IllegalArgumentException("Invalid view type: $viewType")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_QUERY_RATE
            else -> VIEW_TYPE_EXCHANGE_RATE
        }
    }

    // +1 for the first item (query item)
    override fun getItemCount() = differ.currentList.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            0 -> (holder as QueryRateViewHolder)
            else -> (holder as ExchangeRateViewHolder).bind(differ.currentList[position - 1])
        }
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