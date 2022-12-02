package eu.kevin.common.architecture.routing

import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.kevin.common.fragment.FragmentResultContract
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object GlobalRouter {

    private val mainRouterChannel = Channel<Fragment>(capacity = 1)
    val mainRouterFlow = mainRouterChannel.receiveAsFlow()

    private val mainModalRouterChannel = Channel<BottomSheetDialogFragment>(capacity = 1)
    val mainModalRouterFlow = mainModalRouterChannel.receiveAsFlow()

    private val popFragmentChannel = Channel<Fragment?>(capacity = 1)
    val popFragmentFlow = popFragmentChannel.receiveAsFlow()

    private val fragmentResultChannel = Channel<RouterFragmentResultWrapper>(capacity = 1)
    val fragmentResultFlow = fragmentResultChannel.receiveAsFlow()

    fun pushFragment(fragment: Fragment) {
        mainRouterChannel.trySend(fragment)
    }
    fun pushModalFragment(fragment: BottomSheetDialogFragment) {
        mainModalRouterChannel.trySend(fragment)
    }
    fun popCurrentFragment() {
        popFragmentChannel.trySend(null)
    }
    fun <T> returnFragmentResult(contract: FragmentResultContract<T>, result: T) {
        fragmentResultChannel.trySend(RouterFragmentResultWrapper(contract, result))
    }
}