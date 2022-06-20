package eu.kevin.demo.views

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.util.Locale

internal class NumberTextWatcher(
    private val editText: EditText,
    locale: Locale?,
    private val numberOfDecimals: Int
) : TextWatcher {

    private val groupingSeparator: String
    private val decimalSeparator: String
    private val decimalFormat: DecimalFormat
    private val integerDecimalFormat: DecimalFormat

    init {
        val symbols = DecimalFormatSymbols(locale)
        groupingSeparator = symbols.groupingSeparator.toString()
        decimalSeparator = symbols.decimalSeparator.toString()

        val patternInt = "#,###"
        integerDecimalFormat = DecimalFormat(patternInt, symbols)
        val patternDecimal = patternInt + "." + replicate('#', numberOfDecimals)
        decimalFormat = DecimalFormat(patternDecimal, symbols).apply {
            isDecimalSeparatorAlwaysShown = true
            roundingMode = RoundingMode.FLOOR
        }
    }

    override fun afterTextChanged(text: Editable) {
        val value = text.toString().replace(".", decimalSeparator)
        val hasFractionalPart = value.contains(decimalSeparator)

        editText.removeTextChangedListener(this)
        try {
            val startLength = editText.text.length
            var cleanString = value.replace(groupingSeparator, "")
            val number = decimalFormat.parse(cleanString)
            val selectionStart = editText.selectionStart
            if (hasFractionalPart) {
                val decimalPointPosition = cleanString.indexOf(decimalSeparator) + 1
                val decimalLength = cleanString.length - decimalPointPosition
                if (decimalLength > numberOfDecimals) {
                    cleanString = cleanString.substring(0, decimalPointPosition + numberOfDecimals)
                }
                var trailingZeroes = countTrailingZeroes(cleanString)
                var numberString = decimalFormat.format(number)
                while (trailingZeroes-- > 0) {
                    numberString += "0"
                }
                editText.setText(numberString)
            } else {
                editText.setText(integerDecimalFormat.format(number))
            }

            val endLength = editText.text.length
            val newSelection = selectionStart + (endLength - startLength)

            editText.setSelection(
                when {
                    newSelection < 0 -> 0
                    newSelection >= 0 && newSelection <= editText.text.length -> newSelection
                    else -> editText.text.length - 1
                }
            )
        } catch (numberFormatException: NumberFormatException) {
        } catch (parseException: ParseException) {
        }
        editText.addTextChangedListener(this)
    }

    private fun countTrailingZeroes(text: String): Int {
        var count = 0
        for (i in text.length - 1 downTo 0) {
            val char = text[i]
            if ('0' == char) {
                count++
            } else {
                break
            }
        }
        return count
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    private fun replicate(char: Char, n: Int): String {
        return String(CharArray(n)).replace("\u0000", "" + char)
    }
}