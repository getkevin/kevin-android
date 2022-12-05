package eu.kevin.demo.routing

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.kevin.common.architecture.routing.RouterFragmentResultWrapper
import eu.kevin.common.fragment.FragmentResultContract
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

internal object DemoRouter {

    private val mainModalRouterChannel = Channel<BottomSheetDialogFragment>(capacity = 1)
    val mainModalRouterFlow = mainModalRouterChannel.receiveAsFlow()

    private val fragmentResultChannel = Channel<RouterFragmentResultWrapper>(capacity = 1)
    val fragmentResultFlow = fragmentResultChannel.receiveAsFlow()

    fun pushModalFragment(fragment: BottomSheetDialogFragment) {
        mainModalRouterChannel.trySend(fragment)
    }

    fun <T> returnFragmentResult(contract: FragmentResultContract<T>, result: T) {
        fragmentResultChannel.trySend(RouterFragmentResultWrapper(contract, result))
    }
}