package eu.kevin.inapppayments.paymentconfirmation

import android.net.Uri
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.fragment.FragmentResult
import eu.kevin.inapppayments.BuildConfig
import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import eu.kevin.testcore.base.BaseViewModelTest
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PaymentConfirmationViewModelTest : BaseViewModelTest() {

    private lateinit var viewModel: PaymentConfirmationViewModel

    @Before
    override fun setUp() {
        super.setUp()
        viewModel = PaymentConfirmationViewModel(savedStateHandle)
        every { savedStateHandle.get<Any>(any()) } returns null
    }

    @Test
    fun `test handleIntent() Initialize with bank payment`() = mainCoroutineRule.runBlockingTest {
        val paymentId = "1234567"
        val selectedBank = "SWEDBANK_LT"
        val expectedRedirectUrl = BuildConfig.KEVIN_BANK_PAYMENT_URL.format(
            paymentId,
            selectedBank
        )
        val config = PaymentConfirmationFragmentConfiguration(
            paymentId,
            PaymentType.BANK,
            selectedBank,
            skipAuthentication = false
        )

        val states = mutableListOf<PaymentConfirmationState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        viewModel.intents.trySend(PaymentConfirmationIntent.Initialize(config))

        Assert.assertEquals(states.size, 2)
        Assert.assertEquals(states[0].url, "")
        Assert.assertEquals(states[1].url, expectedRedirectUrl)
        job.cancel()
    }

    @Test
    fun `test handleIntent() Initialize with card payment`() = mainCoroutineRule.runBlockingTest {
        val paymentId = "1234567"
        val expectedRedirectUrl = BuildConfig.KEVIN_CARD_PAYMENT_URL.format(paymentId)
        val config = PaymentConfirmationFragmentConfiguration(
            paymentId,
            PaymentType.CARD,
            selectedBank = null,
            skipAuthentication = false
        )

        val states = mutableListOf<PaymentConfirmationState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        viewModel.intents.trySend(PaymentConfirmationIntent.Initialize(config))

        Assert.assertEquals(states.size, 2)
        Assert.assertEquals(states[0].url, "")
        Assert.assertEquals(states[1].url, expectedRedirectUrl)
        job.cancel()
    }

    @Test
    fun `test handleIntent() HandleBackClicked`() = mainCoroutineRule.runBlockingTest {
        mockkObject(GlobalRouter)
        viewModel.intents.trySend(PaymentConfirmationIntent.HandleBackClicked)
        verify(exactly = 1) { GlobalRouter.popCurrentFragment() }
    }

    @Test
    fun `test handleIntent() handlePaymentCompleted success`() = mainCoroutineRule.runBlockingTest {
        val paymentId = "1234567"
        val expectedResult = PaymentConfirmationResult(paymentId)
        val mockUri = mockkClass(Uri::class)
        every { mockUri.getQueryParameter("statusGroup") } returns "completed"
        every { mockUri.getQueryParameter("paymentId") } returns paymentId
        mockkObject(GlobalRouter)
        viewModel.intents.trySend(PaymentConfirmationIntent.HandlePaymentCompleted(mockUri))
        verify(exactly = 1) {
            GlobalRouter.returnFragmentResult(
                PaymentConfirmationContract,
                withArg {
                    Assert.assertTrue((it is FragmentResult.Success) && it.value == expectedResult)
                }
            )
        }
    }

    @Test
    fun `test handleIntent() handlePaymentCompleted failure`() = mainCoroutineRule.runBlockingTest {
        val mockUri = mockkClass(Uri::class)
        every { mockUri.getQueryParameter(any()) } returns ""
        mockkObject(GlobalRouter)
        viewModel.intents.trySend(PaymentConfirmationIntent.HandlePaymentCompleted(mockUri))
        verify(exactly = 1) {
            GlobalRouter.returnFragmentResult(
                PaymentConfirmationContract,
                FragmentResult.Canceled
            )
        }
    }
}