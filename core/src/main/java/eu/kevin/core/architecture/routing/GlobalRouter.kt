package eu.kevin.core.architecture.routing

import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.kevin.core.fragment.FragmentResultContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

object GlobalRouter {

    private val mainRouterChannel = BroadcastChannel<Fragment>(capacity = 1)
    private val mainModalRouterChannel = BroadcastChannel<BottomSheetDialogFragment>(capacity = 1)
    private val popFragmentChannel = BroadcastChannel<Fragment?>(capacity = 1)
    private val fragmentResultChannel = BroadcastChannel<RouterFragmentResultWrapper>(capacity = 1)

    fun pushFragment(fragment: Fragment) = mainRouterChannel.offer(fragment)
    fun pushModalFragment(fragment: BottomSheetDialogFragment) = mainModalRouterChannel.offer(fragment)
    fun popCurrentFragment() = popFragmentChannel.offer(null)
    fun <T>returnFragmentResult(contract: FragmentResultContract<T>, result: T) {
        fragmentResultChannel.offer(RouterFragmentResultWrapper(contract, result))
    }

    fun addOnPushFragmentListener(scope: CoroutineScope, action: (Fragment) -> Unit) {
        scope.launch {
            mainRouterChannel.asFlow().flowOn(Dispatchers.Main).collect { fragment ->
                action.invoke(fragment)
            }
        }
    }

    fun addOnPushModalFragmentListener(scope: CoroutineScope, action: (BottomSheetDialogFragment) -> Unit) {
        scope.launch {
            mainModalRouterChannel.asFlow().flowOn(Dispatchers.Main).collect { fragment ->
                action.invoke(fragment)
            }
        }
    }

    fun addOnPopCurrentFragmentListener(scope: CoroutineScope, action: (Fragment?) -> Unit) {
        scope.launch {
            popFragmentChannel.asFlow().flowOn(Dispatchers.Main).collect { fragment ->
                action.invoke(fragment)
            }
        }
    }

    fun addOnReturnFragmentResultListener(scope: CoroutineScope, action: (RouterFragmentResultWrapper) -> Unit) {
        scope.launch {
            fragmentResultChannel.asFlow().flowOn(Dispatchers.Main).collect { result ->
                action.invoke(result)
            }
        }
    }
}