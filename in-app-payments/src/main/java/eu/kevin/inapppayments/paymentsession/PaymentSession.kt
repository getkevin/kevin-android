package eu.kevin.inapppayments.paymentsession

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.accounts.BuildConfig
import eu.kevin.accounts.bankselection.BankSelectionContract
import eu.kevin.accounts.bankselection.BankSelectionFragmentConfiguration
import eu.kevin.accounts.bankselection.entities.Bank
import eu.kevin.accounts.networking.KevinAccountsClientFactory
import eu.kevin.common.architecture.BaseFlowSession
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.extensions.setFragmentResultListener
import eu.kevin.common.fragment.FragmentResult
import eu.kevin.core.entities.SessionResult
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationContract
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationFragmentConfiguration
import eu.kevin.inapppayments.paymentsession.entities.PaymentSessionConfiguration
import eu.kevin.inapppayments.paymentsession.entities.PaymentSessionData
import eu.kevin.inapppayments.paymentsession.enums.PaymentSessionFlowItem
import eu.kevin.inapppayments.paymentsession.enums.PaymentSessionFlowItem.BANK_SELECTION
import eu.kevin.inapppayments.paymentsession.enums.PaymentSessionFlowItem.PAYMENT_CONFIRMATION
import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val accountsClient = KevinAccountsClientFactory(
        baseUrl = BuildConfig.KEVIN_ACCOUNTS_API_URL,
        userAgent = "",
        timeout = BuildConfig.HTTP_CLIENT_TIMEOUT,
        logLevel = BuildConfig.HTTP_LOGGING_LEVEL
    ).createClient(null)

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
        sessionListener = listener

        if (configuration.paymentType == PaymentType.BANK && configuration.preselectedBank != null) {
            sessionListener?.showLoading(true)
            lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val selectedBank = getSelectedBank()
                withContext(Dispatchers.Main) {
                    sessionListener?.showLoading(false)
                    initializeFlow(selectedBank)
                }
            }
        } else {
            initializeFlow(selectedBank = null)
        }
    }

    private suspend fun getSelectedBank(): Bank? {
        return try {
            val apiBanks = accountsClient.getSupportedBanks(configuration.paymentId, configuration.preselectedCountry?.iso)
            apiBanks.data.firstOrNull { it.id == configuration.preselectedBank }?.let {
                Bank(it.id, it.name, it.officialName, it.imageUri, it.bic)
            }
        } catch (error: Exception) {
            withContext(Dispatchers.Main) {
                sessionListener?.onSessionFinished(SessionResult.Failure(error))
            }
            null
        }
    }

    private fun initializeFlow(selectedBank: Bank?) {
        if (currentFlowIndex == -1) {
            sessionData = sessionData.copy(
                selectedPaymentType = configuration.paymentType,
                selectedCountry = configuration.preselectedCountry?.iso,
                selectedBank = selectedBank
            )
        }
        updateFlowItems()
        if (currentFlowIndex == -1) {
            GlobalRouter.pushFragment(getFlowFragment(0))
        }
    }

    private fun updateFlowItems() {
        val flow = mutableListOf<PaymentSessionFlowItem>()

        if (sessionData.selectedPaymentType == PaymentType.BANK) {
            if (!configuration.skipBankSelection || sessionData.selectedBank == null) {
                flow.add(BANK_SELECTION)
            }
        }

        flow.add(PAYMENT_CONFIRMATION)

        flowItems.clear()
        flowItems.addAll(flow)
    }

    private fun navigateToNextWindow() {
        currentFlowIndex = min(currentFlowIndex + 1, flowItems.size)
        if (currentFlowIndex < flowItems.size) {
            GlobalRouter.pushFragment(getFlowFragment(currentFlowIndex))
        }
    }

    private fun getFlowFragment(index: Int): Fragment {
        return when (flowItems[index]) {
            BANK_SELECTION -> {
                val config = BankSelectionFragmentConfiguration(
                    sessionData.selectedCountry,
                    configuration.disableCountrySelection,
                    configuration.countryFilter,
                    sessionData.selectedBank?.id,
                    configuration.paymentId
                )
                BankSelectionContract.getFragment(config)
            }
            PAYMENT_CONFIRMATION -> {
                val config = PaymentConfirmationFragmentConfiguration(
                    configuration.paymentId,
                    sessionData.selectedPaymentType!!,
                    sessionData.selectedBank?.id,
                )
                PaymentConfirmationContract.getFragment(config)
            }
        }
    }

    private fun listenForFragmentResults() {
        fragmentManager.setFragmentResultListener(BankSelectionContract, lifecycleOwner) {
            sessionData = sessionData.copy(selectedBank = it)
            navigateToNextWindow()
        }
        fragmentManager.setFragmentResultListener(PaymentConfirmationContract, lifecycleOwner) { result ->
            when (result) {
                is FragmentResult.Success -> {
                    sessionListener?.onSessionFinished(
                        SessionResult.Success(PaymentSessionResult(configuration.paymentId))
                    )
                }
                is FragmentResult.Canceled -> sessionListener?.onSessionFinished(SessionResult.Canceled)
            }
        }
    }
}