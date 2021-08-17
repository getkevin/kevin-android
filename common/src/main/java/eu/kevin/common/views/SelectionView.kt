package eu.kevin.common.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import eu.kevin.common.R
import eu.kevin.common.databinding.ViewSelectionBinding

class SelectionView(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    var title: String = ""
        set(value) {
            field = value
            binding.titleView.text = value
        }

    var image: Drawable? = null
        set(value) {
            field = value
            binding.imageView.setImageDrawable(value)
        }

    private val binding = ViewSelectionBinding.inflate(LayoutInflater.from(context), this)

    init {
        background = ContextCompat.getDrawable(context, R.drawable.selection_background)
    }
}