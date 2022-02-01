package eu.kevin.common.extensions

import android.content.Context
import org.joda.money.Money
import org.joda.money.format.MoneyFormatterBuilder

fun Money.toDisplayString(context: Context): String {
    val symbol = MoneyFormatterBuilder()
        .appendCurrencySymbolLocalized()
        .toFormatter(context.getCurrentLocale())
        .print(this)
    val amountString = MoneyFormatterBuilder()
        .appendAmountLocalized()
        .toFormatter(context.getCurrentLocale())
        .print(this)
    return "%1\$s %2\$s".format(symbol, amountString)
}