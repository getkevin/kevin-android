package eu.kevin.common.helpers

import android.view.View
import com.google.android.material.snackbar.Snackbar
import eu.kevin.common.extensions.getColorCompat
import eu.kevin.core.R

object SnackbarHelper {
    fun showError(contextView: View, message: String, duration: Int = Snackbar.LENGTH_LONG) {
        val context = contextView.context
        Snackbar.make(contextView, message, duration)
            .setBackgroundTint(context.getColorCompat(R.color.kevin_warning_red))
            .setTextColor(context.getColorCompat(R.color.kevin_black))
            .show()
    }
}