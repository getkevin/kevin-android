package eu.kevin.inapppayments.cardpayment.inputmask

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

internal class CardNumberInputMask(val input: EditText) {

    fun listen() {
        input.addTextChangedListener(mDateEntryWatcher)
    }

    private val mDateEntryWatcher = object : TextWatcher {

        var edited = false
        val dividerCharacter = " "

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (edited) {
                edited = false
                return
            }

            var working = getEditText()

            working = manageDateDivider(working, 4, start, before)
            working = manageDateDivider(working, 9, start, before)
            working = manageDateDivider(working, 14, start, before)
            working = manageDateDivider(working, 19, start, before)

            edited = true
            input.setText(working)
            input.setSelection(input.text.length)
        }

        private fun manageDateDivider(
            working: String,
            position: Int,
            start: Int,
            before: Int
        ): String {
            if (working.length == position) {
                return if (before <= position && start < position)
                    working + dividerCharacter
                else
                    working.dropLast(1)
            }
            return working
        }

        private fun getEditText(): String {
            return input.text.toString()
        }

        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    }
}