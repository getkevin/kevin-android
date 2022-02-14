package eu.kevin.demo.extensions

import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import java.util.*

fun View.getCurrentLocale(): Locale {
    return this.context.resources.configuration.locales[0]
}

fun View.setDebounceClickListener(debounceTime: Long = 600L, action: () -> Unit) {
    setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}