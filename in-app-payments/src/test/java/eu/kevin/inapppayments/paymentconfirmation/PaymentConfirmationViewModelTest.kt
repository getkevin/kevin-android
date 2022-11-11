package eu.kevin.inapppayments.paymentconfirmation

import android.net.Uri
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.extensions.appendQuery
import eu.kevin.common.fragment.FragmentResult
import eu.kevin.inapppayments.BuildConfig
import eu.kevin.inapppayments.KevinPaymentsConfiguration
import eu.kevin.inapppayments.KevinPaymentsPlugin
import eu.kevin.inapppayments.common.enums.PaymentStatus
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationEvent.LoadWebPage
import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import eu.kevin.testcore.base.BaseViewModelTest
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PaymentConfirmationViewModelTest : BaseViewModelTest() {

    private lateinit var viewModel: PaymentConfirmationViewModel

    @Before
    override fun setUp() {
        super.setUp()

        KevinPaymentsPlugin.configure(
            KevinPaymentsConfiguration.builder()
                .setCallbackUrl("")
                .build()
        )

        viewModel = PaymentConfirmationViewModel(savedStateHandle)
        every { savedStateHandle.get<Any>(any()) } returns null
    }

    @Test
    fun `test handleIntent() Initialize with bank payment`() = testCoroutineScope.runTest {
        val urlQuery = ""
        val paymentId = "1234567"
        val selectedBank = "SWEDBANK_LT"
        val expectedRedirectUrl = BuildConfig.KEVIN_BANK_PAYMENT_URL.format(
            paymentId,
            selectedBank
        ).appendQuery(urlQuery)
        val config = PaymentConfirmationFragmentConfiguration(
            paymentId,
            PaymentType.BANK,
            selectedBank,
            skipAuthentication = false
        )

        val states = mutableListOf<PaymentConfirmationState>()
        val events = mutableListOf<PaymentConfirmationEvent>()

        val jobStates = launch { viewModel.state.toList(states) }
        val jobEvents = launch { viewModel.events.toList(events) }

        viewModel.intents.trySend(
            PaymentConfirmationIntent.Initialize(
                config,
                urlQuery
            )
        )

        Assert.assertEquals(1, states.size)
        Assert.assertEquals(LoadWebPage(expectedRedirectUrl), events[0])

        jobStates.cancel()
        jobEvents.cancel()
    }

    @Test
    fun `test handleIntent() Initialize with card payment`() = testCoroutineScope.runTest {
        val urlQuery = ""
        val paymentId = "1234567"
        val expectedRedirectUrl = BuildConfig.KEVIN_CARD_PAYMENT_URL.format(paymentId).appendQuery(urlQuery)
        val config = PaymentConfirmationFragmentConfiguration(
            paymentId,
            PaymentType.CARD,
            selectedBank = null,
            skipAuthentication = false
        )

        val states = mutableListOf<PaymentConfirmationState>()
        val events = mutableListOf<PaymentConfirmationEvent>()

        val jobStates = launch { viewModel.state.toList(states) }
        val jobEvents = launch { viewModel.events.toList(events) }

        viewModel.intents.trySend(
            PaymentConfirmationIntent.Initialize(
                config,
                ""
            )
        )

        Assert.assertEquals(1, states.size)
        Assert.assertEquals(LoadWebPage(expectedRedirectUrl), events[0])

        jobStates.cancel()
        jobEvents.cancel()
    }

    @Test
    fun `test handleIntent() HandleBackClicked`() = testCoroutineScope.runTest {
        mockkObject(GlobalRouter)
        viewModel.intents.trySend(PaymentConfirmationIntent.HandleBackClicked)
        verify(exactly = 1) { GlobalRouter.popCurrentFragment() }
    }

    @Test
    fun `test handleIntent() handlePaymentCompleted success`() = testCoroutineScope.runTest {
        val paymentId = "1234567"
        val expectedResult = PaymentConfirmationResult(paymentId, PaymentStatus.COMPLETED)
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
    fun `test handleIntent() handlePaymentCompleted failure`() = testCoroutineScope.runTest {
        val mockUri = mockkClass(Uri::class)
        every { mockUri.getQueryParameter(any()) } returns ""
        mockkObject(GlobalRouter)
        viewModel.intents.trySend(PaymentConfirmationIntent.HandlePaymentCompleted(mockUri))
        verify(exactly = 1) {
            GlobalRouter.returnFragmentResult(
                PaymentConfirmationContract,
                withArg {
                    Assert.assertTrue((it is FragmentResult.Failure) && it.error.message == "Payment was canceled!")
                }
            )
        }
    }
}