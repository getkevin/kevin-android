package eu.kevin.testcore.base

import androidx.lifecycle.SavedStateHandle
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before

@ExperimentalCoroutinesApi
abstract class BaseViewModelTest : BaseUnitTest() {

    @MockK
    protected lateinit var savedStateHandle: SavedStateHandle

    @Before
    override fun setUp() {
        super.setUp()
        every { savedStateHandle.get<Any>(any()) } returns null
    }
}