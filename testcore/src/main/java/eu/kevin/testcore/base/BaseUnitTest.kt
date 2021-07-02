package eu.kevin.testcore.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import eu.kevin.testcore.coroutines.MainCoroutineRule
import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule

@ExperimentalCoroutinesApi
abstract class BaseUnitTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    open fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
    }
}