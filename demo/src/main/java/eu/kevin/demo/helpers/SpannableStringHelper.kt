package eu.kevin.demo.helpers

import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View

internal object SpannableStringHelper {

    fun getSpannableWithLinks(
        text: String,
        linkColor: Int,
        vararg links: SpannableStringLink
    ): SpannableStringBuilder {
        val spannableString = SpannableStringBuilder(text)
        links.forEach {
            val startIndex = text.indexOf(it.text)
            val endIndex = startIndex + it.text.length

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    it.onClick()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }
            spannableString.setSpan(ForegroundColorSpan(linkColor), startIndex, endIndex, SPAN_EXCLUSIVE_INCLUSIVE)
            spannableString.setSpan(clickableSpan, startIndex, endIndex, SPAN_EXCLUSIVE_INCLUSIVE)
        }

        return spannableString
    }
}

internal data class SpannableStringLink(
    val text: String,
    val onClick: () -> Unit
)