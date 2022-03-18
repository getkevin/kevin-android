package eu.kevin.testcore.dispatchers

import eu.kevin.common.dispatchers.CoroutineDispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
object TestCoroutineDispatchers : CoroutineDispatchers {
    override val main = UnconfinedTestDispatcher()
    override val io = UnconfinedTestDispatcher()
    override val default = UnconfinedTestDispatcher()
    override val unconfined = UnconfinedTestDispatcher()
}