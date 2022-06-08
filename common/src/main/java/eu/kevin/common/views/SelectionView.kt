package eu.kevin.common.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.widget.TextViewCompat
import eu.kevin.common.R
import eu.kevin.common.databinding.KevinViewSelectionBinding

class SelectionView @JvmOverloads constructor(
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

    private val binding = KevinViewSelectionBinding.inflate(LayoutInflater.from(context), this)

    init {
        val styledAttributes = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SelectionView,
            0,
            0
        )
        val backgroundDrawable = styledAttributes.getDrawable(R.styleable.SelectionView_android_background)
        val textAppearance = styledAttributes.getResourceId(
            R.styleable.SelectionView_android_textAppearance,
            R.style.Kevin_Text_Title1
        )
        background = backgroundDrawable
        TextViewCompat.setTextAppearance(binding.titleView, textAppearance)

        styledAttributes.recycle()
    }
}