package eu.kevin.sample.samples.uicustomization

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import eu.kevin.sample.databinding.KevinActivityUiCustomBinding

internal class UiCustomizationActivity : AppCompatActivity() {

    private lateinit var binding: KevinActivityUiCustomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = KevinActivityUiCustomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            backButton.setOnClickListener {
                finish()
            }
        }
    }
}