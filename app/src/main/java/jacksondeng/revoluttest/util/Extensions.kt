package jacksondeng.revoluttest.util

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.airbnb.lottie.LottieAnimationView


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

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)

fun LottieAnimationView.stopAndHide() {
    this.gone()
    this.pauseAnimation()
}

fun LottieAnimationView.showAndPlay() {
    this.visible()
    this.playAnimation()
}

fun Activity.hideSoftKeyboard() {
    currentFocus?.let {
        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager!!.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}


fun Double.checkForOverflow(multiplier: Double = 1.0): Boolean {
    return (this * multiplier == Double.POSITIVE_INFINITY || this * multiplier == Double.NEGATIVE_INFINITY)
}

