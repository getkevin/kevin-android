package eu.kevin.testcore.base

import androidx.lifecycle.SavedStateHandle
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Before

@ExperimentalCoroutinesApi
abstract class BaseViewModelTest : BaseUnitTest() {

    @MockK
    protected lateinit var savedStateHandle: SavedStateHandle

    protected val testCoroutineDispatcher = TestCoroutineDispatcher()

    @Before
    override fun setUp() {
        super.setUp()
        every { savedStateHandle.get<Any>(any()) } returns null
    }
}