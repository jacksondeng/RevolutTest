package jacksondeng.revoluttest.view.adapter

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.subjects.PublishSubject
import jacksondeng.revoluttest.BuildConfig
import jacksondeng.revoluttest.R
import jacksondeng.revoluttest.databinding.ItemExchangeRateBinding
import jacksondeng.revoluttest.databinding.ItemQueryRateBinding
import jacksondeng.revoluttest.model.entity.CurrencyModel
import jacksondeng.revoluttest.util.*
import jacksondeng.revoluttest.view.viewholder.ExchangeRateViewHolder
import jacksondeng.revoluttest.view.viewholder.QueryRateViewHolder
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

const val VIEW_TYPE_QUERY_RATE = 0
const val VIEW_TYPE_EXCHANGE_RATE = 1

class RatesAdapter(private val sharePref: SharedPreferences) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val clickSubject = PublishSubject.create<CurrencyModel>()
    val textChangeSubject = PublishSubject.create<Double>()
    val focusChangesSubject = PublishSubject.create<Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_QUERY_RATE -> {
                val holder = createQueryRateHolder(parent)
                subscibeToFocusChangeEvent(holder)
                subscribeToKeyboardDoneEvent(parent, holder)
                subscribeToTextChangesEvent(holder)
                return holder
            }

            VIEW_TYPE_EXCHANGE_RATE -> {
                val holder = createExchangeRateHolder(parent)
                subscribeToItemClickEvent(parent, holder)
                return holder
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
    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            0 -> (holder as QueryRateViewHolder).bind(differ.currentList[position])
            else -> (holder as ExchangeRateViewHolder).bind(differ.currentList[position])
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<CurrencyModel>() {
        override fun areItemsTheSame(oldItem: CurrencyModel, newItem: CurrencyModel): Boolean {
            return oldItem.currency.currencyCode == newItem.currency.currencyCode
        }

        override fun areContentsTheSame(oldItem: CurrencyModel, newItem: CurrencyModel): Boolean {
            return oldItem.currency.currencyCode == newItem.currency.currencyCode && oldItem.rate.toString() == newItem.rate.toString()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<CurrencyModel>?) = differ.submitList((list))

    private fun createExchangeRateHolder(parent: ViewGroup): ExchangeRateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemExchangeRateBinding = DataBindingUtil.inflate(
            layoutInflater, R.layout.item_exchange_rate, parent, false
        )
        return ExchangeRateViewHolder(binding)
    }

    private fun createQueryRateHolder(parent: ViewGroup): QueryRateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemQueryRateBinding = DataBindingUtil.inflate(
            layoutInflater, R.layout.item_query_rate, parent, false
        )
        return QueryRateViewHolder(binding)
    }

    private fun subscibeToFocusChangeEvent(holder: QueryRateViewHolder) {
        RxView.focusChanges(holder.binding.queryAmount)
            .skipInitialValue()
            .subscribe(focusChangesSubject)
    }

    @SuppressLint("CheckResult")
    private fun subscribeToKeyboardDoneEvent(parent: ViewGroup, holder: QueryRateViewHolder) {
        RxTextView
            .editorActions(holder.binding.queryAmount)
            .filter { it in arrayOf(EditorInfo.IME_ACTION_DONE, it == EditorInfo.IME_NULL) }
            .subscribe({
                holder.binding.queryAmount.clearFocus()
                parent.hideKeyBoard()
            }, {
                if (BuildConfig.DEBUG) {
                    Log.e("Crash", it.toString())
                }
            })
    }

    @SuppressLint("CheckResult")
    private fun subscribeToTextChangesEvent(holder: QueryRateViewHolder) {
        RxTextView
            .textChanges(holder.binding.queryAmount)
            .debounce(300, TimeUnit.MILLISECONDS)
            .map {
                if (it.isEmpty()) {
                    1.0
                } else {
                    try {
                        val rate = it.toString().toDouble()
                        if (rate.checkForOverflow()) {
                            DecimalFormat(CURRENCY_PATTERN).format(1.0).toString().toDouble()
                        } else {
                            DecimalFormat(CURRENCY_PATTERN).format(rate).toString().toDouble()
                        }
                    } catch (exception: NumberFormatException) {
                        // Set the amount to default (1.0) if overflow happen
                        if (BuildConfig.DEBUG) {
                            Log.e("InvalidNumber", it.toString())
                        }
                        1.0
                    }
                }
            }
            .subscribe(textChangeSubject)
    }

    private fun subscribeToItemClickEvent(parent: ViewGroup, holder: ExchangeRateViewHolder) {
        RxView.clicks(holder.binding.root)
            .takeUntil(RxView.detaches(parent))
            .map<CurrencyModel> {
                sharePref.clearLastCachedTime()
                sharePref.updateSelectedBase(differ.currentList[holder.adapterPosition].currency.currencyCode)
                differ.currentList[holder.adapterPosition]
            }
            .subscribe(clickSubject)
    }
}
