package eu.kevin.testcore.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.MockKAnnotations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule

@ExperimentalCoroutinesApi
abstract class BaseUnitTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    protected val testCoroutineScope = TestScope(UnconfinedTestDispatcher())

    @Before
    open fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        MockKAnnotations.init(this, relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}