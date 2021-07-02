package eu.kevin.accounts.accountlinking

import android.net.Uri
import eu.kevin.accounts.BuildConfig
import eu.kevin.core.architecture.routing.GlobalRouter
import eu.kevin.core.entities.FragmentResult
import eu.kevin.testcore.base.BaseViewModelTest
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AccountLinkingViewModelTest : BaseViewModelTest() {

    private lateinit var viewModel: AccountLinkingViewModel

    @Before
    override fun setUp() {
        super.setUp()
        viewModel = AccountLinkingViewModel(savedStateHandle)
        every { savedStateHandle.get<Any>(any()) } returns null
    }

    @Test
    fun `test handleIntent() Initialize`() = mainCoroutineRule.runBlockingTest {
        val state = "state"
        val selectedBank = "SWEDBANK_LT"
        val expectedRedirectUrl = BuildConfig.KEVIN_LINK_ACCOUNT_URL.format(
            state,
            selectedBank
        )
        val config = AccountLinkingFragmentConfiguration(state, selectedBank)

        val states = mutableListOf<AccountLinkingState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        viewModel.intents.trySend(AccountLinkingIntent.Initialize(config))

        assertEquals(states.size, 2)
        assertEquals(states[0].bankRedirectUrl, "")
        assertEquals(states[1].bankRedirectUrl, expectedRedirectUrl)
        job.cancel()
    }

    @Test
    fun `test handleIntent() HandleBackClicked`() = mainCoroutineRule.runBlockingTest {
        mockkObject(GlobalRouter)
        viewModel.intents.trySend(AccountLinkingIntent.HandleBackClicked)
        verify(exactly = 1) { GlobalRouter.popCurrentFragment() }
    }

    @Test
    fun `test handleIntent() handleAuthorizationReceived success`() = mainCoroutineRule.runBlockingTest {
        val requestId = "1234567"
        val code = "7654321"
        val expectedResult = AccountLinkingFragmentResult(requestId, code)
        val mockUri = mockkClass(Uri::class)
        every { mockUri.getQueryParameter("status") } returns "success"
        every { mockUri.getQueryParameter("requestId") } returns requestId
        every { mockUri.getQueryParameter("code") } returns code
        mockkObject(GlobalRouter)
        viewModel.intents.trySend(AccountLinkingIntent.HandleAuthorization(mockUri))
        verify(exactly = 1) {
            GlobalRouter.returnFragmentResult(
                AccountLinkingFragment.Contract,
                withArg {
                    assertTrue((it is FragmentResult.Success) && it.value == expectedResult)
                }
            )
        }
    }

    @Test
    fun `test handleIntent() handleAuthorizationReceived failure`() = mainCoroutineRule.runBlockingTest {
        val mockUri = mockkClass(Uri::class)
        every { mockUri.getQueryParameter(any()) } returns ""
        mockkObject(GlobalRouter)
        viewModel.intents.trySend(AccountLinkingIntent.HandleAuthorization(mockUri))
        verify(exactly = 1) {
            GlobalRouter.returnFragmentResult(
                AccountLinkingFragment.Contract,
                FragmentResult.Canceled
            )
        }
    }
}