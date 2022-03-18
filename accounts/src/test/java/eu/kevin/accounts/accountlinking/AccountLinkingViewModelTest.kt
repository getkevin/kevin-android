package eu.kevin.accounts.accountlinking

import android.net.Uri
import eu.kevin.accounts.BuildConfig
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.extensions.appendQuery
import eu.kevin.common.fragment.FragmentResult
import eu.kevin.testcore.base.BaseViewModelTest
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
    fun `test handleIntent() Initialize`() = testCoroutineScope.runTest {
        val urlQuery = ""
        val state = "state"
        val selectedBank = "SWEDBANK_LT"
        val expectedRedirectUrl = BuildConfig.KEVIN_LINK_ACCOUNT_URL.format(
            state,
            selectedBank
        ).appendQuery(urlQuery)
        val config = AccountLinkingFragmentConfiguration(state, selectedBank)

        val states = mutableListOf<AccountLinkingState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        viewModel.intents.trySend(
            AccountLinkingIntent.Initialize(
                config,
                urlQuery
            )
        )

        assertEquals(2, states.size)
        assertEquals("", states[0].bankRedirectUrl)
        assertEquals(expectedRedirectUrl, states[1].bankRedirectUrl)
        job.cancel()
    }

    @Test
    fun `test handleIntent() HandleBackClicked`() = testCoroutineScope.runTest {
        mockkObject(GlobalRouter)
        viewModel.intents.trySend(AccountLinkingIntent.HandleBackClicked)
        verify(exactly = 1) { GlobalRouter.popCurrentFragment() }
    }

    @Test
    fun `test handleIntent() handleAuthorizationReceived success`() = testCoroutineScope.runTest {
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
                AccountLinkingContract,
                withArg {
                    assertTrue((it is FragmentResult.Success) && it.value == expectedResult)
                }
            )
        }
    }

    @Test
    fun `test handleIntent() handleAuthorizationReceived failure`() = testCoroutineScope.runTest {
        val mockUri = mockkClass(Uri::class)
        every { mockUri.getQueryParameter(any()) } returns ""
        mockkObject(GlobalRouter)
        viewModel.intents.trySend(AccountLinkingIntent.HandleAuthorization(mockUri))
        verify(exactly = 1) {
            GlobalRouter.returnFragmentResult(
                AccountLinkingContract,
                FragmentResult.Canceled
            )
        }
    }
}