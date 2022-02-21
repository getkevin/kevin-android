package eu.kevin.demo.routing

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.kevin.common.architecture.routing.RouterFragmentResultWrapper
import eu.kevin.common.fragment.FragmentResultContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

object DemoRouter {

    private val mainModalRouterChannel = Channel<BottomSheetDialogFragment>(capacity = 1)
    private val fragmentResultChannel = Channel<RouterFragmentResultWrapper>(capacity = 1)

    fun pushModalFragment(fragment: BottomSheetDialogFragment) {
        mainModalRouterChannel.trySend(fragment)
    }
    fun <T>returnFragmentResult(contract: FragmentResultContract<T>, result: T) {
        fragmentResultChannel.trySend(RouterFragmentResultWrapper(contract, result))
    }

    fun addOnPushModalFragmentListener(scope: CoroutineScope, action: (BottomSheetDialogFragment) -> Unit) {
        scope.launch {
            mainModalRouterChannel.receiveAsFlow().flowOn(Dispatchers.Main).collect { fragment ->
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