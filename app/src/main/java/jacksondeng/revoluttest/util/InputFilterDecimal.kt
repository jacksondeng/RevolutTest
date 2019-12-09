package jacksondeng.revoluttest.util

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern


class CurrencyFormatInputFilter : InputFilter {
    var desiredPattern: Pattern = Pattern.compile("(0|[1-9]+[0-9]*)?(\\.[0-9]{0,100})?")

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val result = (dest.subSequence(0, dstart).toString()
                + source.toString()
                + dest.subSequence(dend, dest.length))

        val matcher = desiredPattern.matcher(result)

        return if (!matcher.matches()) dest.subSequence(dstart, dend) else null

    }
}