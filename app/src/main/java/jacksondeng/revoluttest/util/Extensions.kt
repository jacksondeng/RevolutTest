package jacksondeng.revoluttest.util

import android.content.SharedPreferences
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

fun EditText.addTextWatcher(): Flowable<EditTextFlow> {
    return Flowable.create<EditTextFlow>({ emitter ->
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                emitter.onNext(EditTextFlow(p0.toString(), EditTextFlow.Type.BEFORE))
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                emitter.onNext(EditTextFlow(p0.toString(), EditTextFlow.Type.ON))
            }

            override fun afterTextChanged(p0: Editable?) {
                emitter.onNext(EditTextFlow(p0.toString(), EditTextFlow.Type.AFTER))
            }
        })
    }, BackpressureStrategy.BUFFER)
}

fun SharedPreferences.updateSelectedBase(base: String) {
    this.edit()
        .putString(TAG_SELECTED_BASE, base)
        .apply()
}

fun SharedPreferences.getSelectedBase(): String {
    return this.getString(TAG_SELECTED_BASE, "EUR") ?: "EUR"
}

fun SharedPreferences.clearLastCachedTime() {
    this.edit()
        .putLong(TAG_LAST_CACHED_TIME, -1L)
        .apply()
}