package eu.kevin.common.helpers

import android.view.View
import com.google.android.material.snackbar.Snackbar
import eu.kevin.core.R
import eu.kevin.common.extensions.getColorCompat

object SnackbarHelper {
    fun showError(contextView: View, message: String, duration: Int = Snackbar.LENGTH_LONG) {
        val context = contextView.context
        Snackbar.make(contextView, message, duration)
            .setBackgroundTint(context.getColorCompat(R.color.kevin_red))
            .setTextColor(context.getColorCompat(R.color.dark_blue))
            .show()
    }
}