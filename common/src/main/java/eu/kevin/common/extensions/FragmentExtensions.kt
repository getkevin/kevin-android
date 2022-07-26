package eu.kevin.common.extensions

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import eu.kevin.common.fragment.FragmentResultContract
import kotlinx.coroutines.launch
import java.io.Serializable

fun <T> Fragment.setFragmentResult(contract: FragmentResultContract<T>, result: T) {
    val resultBundle = Bundle()
    when (result) {
        is Int -> resultBundle.putInt(contract.resultKey, result)
        is Long -> resultBundle.putLong(contract.resultKey, result)
        is Float -> resultBundle.putFloat(contract.resultKey, result)
        is String -> resultBundle.putString(contract.resultKey, result)
        is Bundle -> resultBundle.putBundle(contract.resultKey, result)
        is Serializable -> resultBundle.putSerializable(contract.resultKey, result)
        is Parcelable -> resultBundle.putParcelable(contract.resultKey, result)
    }
    setFragmentResult(contract.requestKey, resultBundle)
}

fun Fragment.launchOnRepeat(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend () -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(lifecycleState) {
            launch { block() }
        }
    }
}