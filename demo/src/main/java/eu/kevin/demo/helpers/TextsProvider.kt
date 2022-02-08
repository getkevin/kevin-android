package eu.kevin.demo.helpers

import android.content.Context

class TextsProvider(private val context: Context) {

    fun provideText(stringRes: Int) =
        context.getString(stringRes)
}