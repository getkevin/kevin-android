package eu.kevin.accounts.accountsession

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.accounts.KevinAccountsPlugin
import eu.kevin.accounts.accountlinking.AccountLinkingContract
import eu.kevin.accounts.accountlinking.AccountLinkingFragmentConfiguration
import eu.kevin.accounts.accountsession.entities.AccountSessionConfiguration
import eu.kevin.accounts.accountsession.entities.AccountSessionData
import eu.kevin.accounts.accountsession.enums.AccountLinkingType
import eu.kevin.accounts.accountsession.enums.AccountSessionFlowItem
import eu.kevin.accounts.accountsession.enums.AccountSessionFlowItem.BANK_SELECTION
import eu.kevin.accounts.accountsession.enums.AccountSessionFlowItem.LINK_ACCOUNT_WEB_VIEW
import eu.kevin.accounts.bankselection.BankSelectionContract
import eu.kevin.accounts.bankselection.BankSelectionFragmentConfiguration
import eu.kevin.accounts.bankselection.entities.Bank
import eu.kevin.accounts.bankselection.entities.SupportedBanksFilter
import eu.kevin.accounts.bankselection.managers.KevinBankManager
import eu.kevin.accounts.bankselection.usecases.GetSupportedBanksUseCase
import eu.kevin.accounts.bankselection.usecases.ValidateBanksConfigUseCase
import eu.kevin.accounts.bankselection.usecases.ValidateBanksConfigUseCase.Status
import eu.kevin.accounts.networking.AccountsClientProvider
import eu.kevin.common.architecture.BaseFlowSession
import eu.kevin.common.architecture.interfaces.DeepLinkHandler
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.dispatchers.DefaultCoroutineDispatchers
import eu.kevin.common.extensions.setFragmentResultListener
import eu.kevin.common.fragment.FragmentResult
import eu.kevin.core.entities.SessionResult
import kotlinx.coroutines.launch
import kotlin.math.min

internal class AccountSession(
    private val fragmentManager: FragmentManager,
    private val configuration: AccountSessionConfiguration,
    private val lifecycleOwner: LifecycleOwner,
    registryOwner: SavedStateRegistryOwner
) : BaseFlowSession(lifecycleOwner, registryOwner), DefaultLifecycleObserver {

    private val validateBanksConfigUseCase = ValidateBanksConfigUseCase(
        dispatchers = DefaultCoroutineDispatchers,
        getSupportedBanksUseCase = GetSupportedBanksUseCase(
            KevinBankManager(AccountsClientProvider.kevinAccountsClient)
        )
    )

    private var sessionListener: AccountSessionListener? = null

    private val backStackListener = FragmentManager.OnBackStackChangedListener {
        currentFlowIndex = fragmentManager.backStackEntryCount - 1
    }

    private val flowItems = mutableListOf<AccountSessionFlowItem>()
    private var currentFlowIndex by savedState(-1)
    private var sessionData by savedState(AccountSessionData())

    init {
        lifecycleOwner.lifecycle.addObserver(this)
        fragmentManager.addOnBackStackChangedListener(backStackListener)
        initFragmentResultListeners()
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

    fun beginFlow(listener: AccountSessionListener?) {
        sessionListener = listener

        if (
            configuration.accountLinkingType == AccountLinkingType.BANK &&
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
                    authState = configuration.state,
                    country = configuration.preselectedCountry?.iso,
                    preselectedBank = configuration.preselectedBank,
                    supportedBanksFilter = SupportedBanksFilter(
                        banks = configuration.bankFilter,
                        showOnlyAccountLinkingSupportedBanks = !KevinAccountsPlugin.isShowUnsupportedBanks()
                    )
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
                selectedCountry = configuration.preselectedCountry?.iso,
                selectedBank = selectedBank,
                linkingType = configuration.accountLinkingType
            )
        }
        updateFlowItems()
        if (currentFlowIndex == -1) {
            GlobalRouter.pushFragment(getFlowFragment(0))
        }
    }

    private fun updateFlowItems() {
        val flow = mutableListOf<AccountSessionFlowItem>()

        if (flowShouldIncludeBankSelection()) {
            flow.add(BANK_SELECTION)
        }

        flow.add(LINK_ACCOUNT_WEB_VIEW)

        flowItems.clear()
        flowItems.addAll(flow)
    }

    private fun flowShouldIncludeBankSelection(): Boolean {
        return sessionData.linkingType == AccountLinkingType.BANK &&
            (!configuration.skipBankSelection || sessionData.selectedBank == null)
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
                    configuration.state,
                    !KevinAccountsPlugin.isShowUnsupportedBanks()
                )
                BankSelectionContract.getFragment(config)
            }
            LINK_ACCOUNT_WEB_VIEW -> {
                val config = AccountLinkingFragmentConfiguration(
                    configuration.state,
                    sessionData.selectedBank?.id,
                    sessionData.linkingType!!
                )
                AccountLinkingContract.getFragment(config)
            }
        }
    }

    private fun initFragmentResultListeners() {
        with(fragmentManager) {
            setFragmentResultListener(BankSelectionContract, lifecycleOwner) { result ->
                when (result) {
                    is FragmentResult.Success -> {
                        sessionData = sessionData.copy(selectedBank = result.value)
                        navigateToNextWindow()
                    }
                    is FragmentResult.Canceled -> sessionListener?.onSessionFinished(SessionResult.Canceled)
                    is FragmentResult.Failure -> sessionListener?.onSessionFinished(
                        SessionResult.Failure(
                            result.error
                        )
                    )
                }
            }
            setFragmentResultListener(AccountLinkingContract, lifecycleOwner) { result ->
                when (result) {
                    is FragmentResult.Success -> {
                        sessionData = sessionData.copy(authorization = result.value.authCode)
                        sessionListener?.onSessionFinished(
                            SessionResult.Success(
                                AccountSessionResult(
                                    sessionData.authorization!!,
                                    sessionData.selectedBank,
                                    sessionData.linkingType!!
                                )
                            )
                        )
                    }
                    is FragmentResult.Canceled -> sessionListener?.onSessionFinished(SessionResult.Canceled)
                    is FragmentResult.Failure -> sessionListener?.onSessionFinished(
                        SessionResult.Failure(
                            result.error
                        )
                    )
                }
            }
        }
    }
}