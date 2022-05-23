package eu.kevin.inapppayments.cardpayment.inputformatters

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher

internal class DateFormatter : TextWatcher {
    private var current = ""
    private val nonDigits = Regex("[^\\d]")

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable) {
        if (s.toString() != current) {
            val userInput = s.toString().replace(nonDigits, "")
            if (userInput.length <= 4) {
                current = userInput.chunked(2).joinToString("/")
                s.filters = arrayOfNulls<InputFilter>(0)
            }
            s.replace(0, s.length, current, 0, current.length)
        }
    }
}