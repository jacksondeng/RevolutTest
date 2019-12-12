package jacksondeng.revoluttest.view.viewholder

import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.recyclerview.widget.RecyclerView
import jacksondeng.revoluttest.databinding.ItemQueryRateBinding
import jacksondeng.revoluttest.model.entity.CurrencyModel
import jacksondeng.revoluttest.util.CurrencyFormatInputFilter
import jacksondeng.revoluttest.util.EditTextFlow
import jacksondeng.revoluttest.util.addTextWatcher
import jacksondeng.revoluttest.view.adapter.InterActionListener
import java.util.concurrent.TimeUnit


class QueryRateViewHolder(
    private val binding: ItemQueryRateBinding,
    private val interActionListener: InterActionListener
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(currency: CurrencyModel) {
        binding.obj = currency
        binding.executePendingBindings()
        binding.queryAmount.filters = arrayOf(CurrencyFormatInputFilter())
        interActionListener.getInputStream(flow = binding.queryAmount.addTextWatcher()
            .filter { it.type == EditTextFlow.Type.AFTER }
            .debounce(150, TimeUnit.MILLISECONDS)
            .map { it.query })

        binding.queryAmount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                interActionListener.onFocusRequested()
            } else {
                interActionListener.onFocusLost(binding.queryAmount.text.toString().toDouble())
            }
        }

        binding.queryAmount.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.clearFocus()
                return@OnEditorActionListener true
            }
            false
        })
    }
}
