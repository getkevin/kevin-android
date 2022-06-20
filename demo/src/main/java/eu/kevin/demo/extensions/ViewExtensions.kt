package eu.kevin.demo.extensions

import android.os.SystemClock
import android.view.View
import eu.kevin.common.extensions.getCurrentLocale

internal fun View.getCurrentLocale() = context.getCurrentLocale()

internal fun View.setDebounceClickListener(debounceTime: Long = 600L, action: () -> Unit) {
    setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}