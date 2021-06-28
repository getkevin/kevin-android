package eu.kevin.inapppayments.paymentsession

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.accounts.bankselection.BankSelectionFragment
import eu.kevin.accounts.bankselection.BankSelectionFragmentConfiguration
import eu.kevin.core.architecture.BaseFlowSession
import eu.kevin.core.architecture.routing.GlobalRouter
import eu.kevin.core.entities.ActivityResult
import eu.kevin.core.entities.FragmentResult
import eu.kevin.core.extensions.setFragmentResultListener
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationFragment
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationFragmentConfiguration
import eu.kevin.inapppayments.paymentsession.entities.PaymentSessionConfiguration
import eu.kevin.inapppayments.paymentsession.entities.PaymentSessionData
import eu.kevin.inapppayments.paymentsession.enums.PaymentSessionFlowItem
import eu.kevin.inapppayments.paymentsession.enums.PaymentSessionFlowItem.*
import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import kotlin.math.min

internal class PaymentSession(
    private val fragmentManager: FragmentManager,
    private val configuration: PaymentSessionConfiguration,
    private val lifecycleOwner: LifecycleOwner,
    registryOwner: SavedStateRegistryOwner
) : BaseFlowSession(lifecycleOwner, registryOwner), LifecycleObserver {

    private val backStackListener = FragmentManager.OnBackStackChangedListener {
        currentFlowIndex = fragmentManager.backStackEntryCount - 1
    }

    private var sessionListener: PaymentSessionListener? = null

    private val flowItems = mutableListOf<PaymentSessionFlowItem>()
    private var currentFlowIndex by savedState(-1)
    private var sessionData by savedState(PaymentSessionData())

    init {
        lifecycleOwner.lifecycle.addObserver(this)
        fragmentManager.addOnBackStackChangedListener(backStackListener)
        listenForFragmentResults()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        sessionListener = null
    }

    fun beginFlow(listener: PaymentSessionListener) {
        this.sessionListener = listener
        if (currentFlowIndex == -1) {
            sessionData = sessionData.copy(
                selectedPaymentType = configuration.paymentType,
                selectedCountry = configuration.preselectedCountry?.iso,
                selectedBankId = configuration.preselectedBank
            )
        }
        updateFlow()
        if (currentFlowIndex == -1) {
            GlobalRouter.pushFragment(getFlowFragment(0))
        }
    }

    private fun updateFlow() {
        val flow = mutableListOf<PaymentSessionFlowItem>()

        if (sessionData.selectedPaymentType == PaymentType.BANK) {
            if (!configuration.skipBankSelection) {
                flow.add(BANK_SELECTION)
            }
        }

        flow.add(PAYMENT_CONFIRMATION)

        flowItems.clear()
        flowItems.addAll(flow)
    }

    private fun handleFowNavigation() {
        currentFlowIndex = min(currentFlowIndex + 1, flowItems.size)
        if (flowItems.size == currentFlowIndex) {
            sessionListener?.onSessionFinished(
                ActivityResult.Success(PaymentSessionResult(configuration.paymentId))
            )
        } else {
            GlobalRouter.pushFragment(getFlowFragment(currentFlowIndex))
        }
    }

    private fun getFlowFragment(index: Int): Fragment {
        return when (flowItems[index]) {
            BANK_SELECTION -> {
                BankSelectionFragment().also {
                    it.configuration = BankSelectionFragmentConfiguration(
                        sessionData.selectedCountry,
                        configuration.disableCountrySelection,
                        configuration.countriesFilter,
                        sessionData.selectedBankId,
                        configuration.paymentId
                    )
                }
            }
            PAYMENT_CONFIRMATION -> {
                PaymentConfirmationFragment().also {
                    it.configuration = PaymentConfirmationFragmentConfiguration(
                        configuration.paymentId,
                        sessionData.selectedPaymentType!!,
                        sessionData.selectedBankId,
                    )
                }
            }
        }
    }

    private fun listenForFragmentResults() {
        fragmentManager.setFragmentResultListener(BankSelectionFragment.Contract, lifecycleOwner) {
            sessionData = sessionData.copy(selectedBankId = it.id)
            handleFowNavigation()
        }
        fragmentManager.setFragmentResultListener(PaymentConfirmationFragment.Contract, lifecycleOwner) { result ->
            when (result) {
                is FragmentResult.Success -> {
                    handleFowNavigation()
                }
                is FragmentResult.Canceled -> sessionListener?.onSessionFinished(ActivityResult.Canceled)
            }
        }
    }
}