package eu.kevin.inapppayments.paymentsession

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.accounts.bankselection.BankSelectionContract
import eu.kevin.accounts.bankselection.BankSelectionFragmentConfiguration
import eu.kevin.accounts.bankselection.entities.Bank
import eu.kevin.accounts.bankselection.entities.SupportedBanksFilter
import eu.kevin.accounts.bankselection.managers.KevinBankManager
import eu.kevin.accounts.bankselection.usecases.GetSupportedBanksUseCase
import eu.kevin.accounts.bankselection.usecases.ValidateBanksConfigUseCase
import eu.kevin.accounts.bankselection.usecases.ValidateBanksConfigUseCase.Status
import eu.kevin.common.architecture.BaseFlowSession
import eu.kevin.common.architecture.interfaces.DeepLinkHandler
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.dispatchers.DefaultCoroutineDispatchers
import eu.kevin.common.extensions.setFragmentResultListener
import eu.kevin.common.fragment.FragmentResult
import eu.kevin.core.entities.SessionResult
import eu.kevin.inapppayments.networking.AccountsClientProvider
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationContract
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationFragmentConfiguration
import eu.kevin.inapppayments.paymentsession.entities.PaymentSessionConfiguration
import eu.kevin.inapppayments.paymentsession.entities.PaymentSessionData
import eu.kevin.inapppayments.paymentsession.enums.PaymentSessionFlowItem
import eu.kevin.inapppayments.paymentsession.enums.PaymentSessionFlowItem.BANK_SELECTION
import eu.kevin.inapppayments.paymentsession.enums.PaymentSessionFlowItem.PAYMENT_CONFIRMATION
import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import kotlinx.coroutines.launch
import kotlin.math.min

internal class PaymentSession(
    private val fragmentManager: FragmentManager,
    private val configuration: PaymentSessionConfiguration,
    private val lifecycleOwner: LifecycleOwner,
    registryOwner: SavedStateRegistryOwner
) : BaseFlowSession(lifecycleOwner, registryOwner), DefaultLifecycleObserver {

    private val validateBanksConfigUseCase = ValidateBanksConfigUseCase(
        dispatchers = DefaultCoroutineDispatchers,
        getSupportedBanksUseCase = GetSupportedBanksUseCase(
            KevinBankManager(AccountsClientProvider.kevinAccountsClient)
        )
    )

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

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        sessionListener = null
    }

    override fun handleDeepLink(uri: Uri) {
        val deepLinkHandler = fragmentManager.fragments.firstOrNull {
            it is DeepLinkHandler
        } as? DeepLinkHandler
        deepLinkHandler?.handleDeepLink(uri)
    }

    fun beginFlow(listener: PaymentSessionListener) {
        sessionListener = listener

        if (
            !configuration.skipAuthentication && configuration.paymentType == PaymentType.BANK &&
            (!configuration.preselectedBank.isNullOrBlank() || configuration.bankFilter.isNotEmpty())
        ) {
            validateBanksAndInitializeFlow()
        } else {
            initializeFlow(selectedBank = null)
        }
    }

    private fun validateBanksAndInitializeFlow() {
        sessionListener?.showLoading(true)
        lifecycleOwner.lifecycleScope.launch {
            try {
                val banksConfigStatus = validateBanksConfigUseCase.validateBanksConfig(
                    authState = configuration.paymentId,
                    country = configuration.preselectedCountry?.iso,
                    preselectedBank = configuration.preselectedBank,
                    supportedBanksFilter = SupportedBanksFilter(configuration.bankFilter)
                )

                when (banksConfigStatus) {
                    is Status.FiltersInvalid -> {
                        sessionListener?.onSessionFinished(
                            SessionResult.Failure(Error("Provided bank filter does not contain supported banks"))
                        )
                    }
                    is Status.PreselectedInvalid -> {
                        sessionListener?.onSessionFinished(
                            SessionResult.Failure(Error("Provided preselected bank is not supported"))
                        )
                    }
                    is Status.Valid -> {
                        val selectedBank = banksConfigStatus.selectedBank
                            ?.let { Bank(it.id, it.name, it.officialName, it.imageUri, it.bic) }

                        sessionListener?.showLoading(false)
                        initializeFlow(selectedBank)
                    }
                }
            } catch (error: Exception) {
                sessionListener?.onSessionFinished(SessionResult.Failure(error))
            }
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

        if (sessionData.selectedPaymentType == PaymentType.BANK && !configuration.skipAuthentication) {
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
                    configuration.bankFilter,
                    sessionData.selectedBank?.id,
                    configuration.paymentId,
                    showOnlyAccountLinkingSupportedBanks = false
                )
                BankSelectionContract.getFragment(config)
            }
            PAYMENT_CONFIRMATION -> {
                val config = PaymentConfirmationFragmentConfiguration(
                    configuration.paymentId,
                    sessionData.selectedPaymentType!!,
                    sessionData.selectedBank?.id,
                    configuration.skipAuthentication
                )
                PaymentConfirmationContract.getFragment(config)
            }
        }
    }

    private fun listenForFragmentResults() {
        fragmentManager.setFragmentResultListener(BankSelectionContract, lifecycleOwner) { result ->
            when (result) {
                is FragmentResult.Success -> {
                    sessionData = sessionData.copy(selectedBank = result.value)
                    navigateToNextWindow()
                }
                is FragmentResult.Canceled -> sessionListener?.onSessionFinished(SessionResult.Canceled)
                is FragmentResult.Failure -> sessionListener?.onSessionFinished(SessionResult.Failure(result.error))
            }
        }
        fragmentManager.setFragmentResultListener(PaymentConfirmationContract, lifecycleOwner) { result ->
            when (result) {
                is FragmentResult.Success -> {
                    sessionListener?.onSessionFinished(
                        SessionResult.Success(PaymentSessionResult(configuration.paymentId))
                    )
                }
                is FragmentResult.Canceled -> sessionListener?.onSessionFinished(SessionResult.Canceled)
                is FragmentResult.Failure -> sessionListener?.onSessionFinished(SessionResult.Failure(result.error))
            }
        }
    }
}