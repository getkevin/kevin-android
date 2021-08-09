package eu.kevin.common.architecture.routing

import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.kevin.common.fragment.FragmentResultContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

object GlobalRouter {

    private val mainRouterChannel = Channel<Fragment>(capacity = 1)
    private val mainModalRouterChannel = Channel<BottomSheetDialogFragment>(capacity = 1)
    private val popFragmentChannel = Channel<Fragment?>(capacity = 1)
    private val fragmentResultChannel = Channel<RouterFragmentResultWrapper>(capacity = 1)

    fun pushFragment(fragment: Fragment) {
        mainRouterChannel.trySend(fragment)
    }
    fun pushModalFragment(fragment: BottomSheetDialogFragment) {
        mainModalRouterChannel.trySend(fragment)
    }
    fun popCurrentFragment() {
        popFragmentChannel.trySend(null)
    }
    fun <T>returnFragmentResult(contract: FragmentResultContract<T>, result: T) {
        fragmentResultChannel.trySend(RouterFragmentResultWrapper(contract, result))
    }

    fun addOnPushFragmentListener(scope: CoroutineScope, action: (Fragment) -> Unit) {
        scope.launch {
            mainRouterChannel.receiveAsFlow().flowOn(Dispatchers.Main).collect { fragment ->
                action.invoke(fragment)
            }
        }
    }

    fun addOnPushModalFragmentListener(scope: CoroutineScope, action: (BottomSheetDialogFragment) -> Unit) {
        scope.launch {
            mainModalRouterChannel.receiveAsFlow().flowOn(Dispatchers.Main).collect { fragment ->
                action.invoke(fragment)
            }
        }
    }

    fun addOnPopCurrentFragmentListener(scope: CoroutineScope, action: (Fragment?) -> Unit) {
        scope.launch {
            popFragmentChannel.receiveAsFlow().flowOn(Dispatchers.Main).collect { fragment ->
                action.invoke(fragment)
            }
        }
    }

    fun addOnReturnFragmentResultListener(scope: CoroutineScope, action: (RouterFragmentResultWrapper) -> Unit) {
        scope.launch {
            fragmentResultChannel.receiveAsFlow().flowOn(Dispatchers.Main).collect { result ->
                action.invoke(result)
            }
        }
    }
}