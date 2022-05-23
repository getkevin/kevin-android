package eu.kevin.common.extensions

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import eu.kevin.common.fragment.FragmentResultContract
import java.io.Serializable

fun <T> FragmentManager.setFragmentResultListener(
    contract: FragmentResultContract<T>,
    lifecycleOwner: LifecycleOwner,
    callback: (T) -> Unit
) {
    setFragmentResultListener(contract.requestKey, lifecycleOwner) { _, bundle ->
        callback.invoke(contract.parseResult(bundle))
    }
}

fun FragmentManager.setFragmentResult(contract: FragmentResultContract<*>, result: Any?) {
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