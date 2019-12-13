package jacksondeng.revoluttest.util

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.view.inputmethod.InputMethodManager

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

fun View.hideKeyBoard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.hideSoftInputFromWindow(this.windowToken, 0)
}