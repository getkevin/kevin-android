package eu.kevin.accounts.accountsession

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.accounts.accountlinking.AccountLinkingContract
import eu.kevin.accounts.accountlinking.AccountLinkingFragmentConfiguration
import eu.kevin.accounts.accountsession.entities.AccountSessionConfiguration
import eu.kevin.accounts.accountsession.entities.AccountSessionData
import eu.kevin.accounts.accountsession.enums.AccountSessionFlowItem
import eu.kevin.accounts.accountsession.enums.AccountSessionFlowItem.BANK_SELECTION
import eu.kevin.accounts.accountsession.enums.AccountSessionFlowItem.LINK_ACCOUNT_WEB_VIEW
import eu.kevin.accounts.accountsession.enums.AccountLinkingType
import eu.kevin.accounts.bankselection.BankSelectionContract
import eu.kevin.accounts.bankselection.BankSelectionFragmentConfiguration
import eu.kevin.accounts.bankselection.entities.Bank
import eu.kevin.accounts.networking.AccountsClientProvider
import eu.kevin.common.architecture.BaseFlowSession
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.extensions.setFragmentResultListener
import eu.kevin.common.fragment.FragmentResult
import eu.kevin.core.entities.SessionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min

internal class AccountSession(
    private val fragmentManager: FragmentManager,
    private val configuration: AccountSessionConfiguration,
    private val lifecycleOwner: LifecycleOwner,
    registryOwner: SavedStateRegistryOwner
) : BaseFlowSession(lifecycleOwner, registryOwner), DefaultLifecycleObserver {

    private var sessionListener: AccountSessionListener? = null

    private val backStackListener = FragmentManager.OnBackStackChangedListener {
        currentFlowIndex = fragmentManager.backStackEntryCount - 1
    }

    private val flowItems = mutableListOf<AccountSessionFlowItem>()
    private val accountsClient = AccountsClientProvider.kevinAccountsClient
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

    fun beginFlow(listener: AccountSessionListener?) {
        sessionListener = listener

        if (configuration.accountLinkingType == AccountLinkingType.BANK && configuration.preselectedBank != null) {
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

    private suspend fun getSelectedBank(): Bank? {
        return try {
            val apiBanks = accountsClient.getSupportedBanks(configuration.state, configuration.preselectedCountry?.iso)
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

    private fun updateFlowItems() {
        val flow = mutableListOf<AccountSessionFlowItem>()

        if (sessionData.linkingType == AccountLinkingType.BANK) {
            if (!configuration.skipBankSelection || sessionData.selectedBank == null) {
                flow.add(BANK_SELECTION)
            }
        }

        flow.add(LINK_ACCOUNT_WEB_VIEW)

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
                    configuration.state
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
            setFragmentResultListener(BankSelectionContract, lifecycleOwner) { selectedBank ->
                sessionData = sessionData.copy(selectedBank = selectedBank)
                navigateToNextWindow()
            }
            setFragmentResultListener(AccountLinkingContract, lifecycleOwner) { result ->
                when (result) {
                    is FragmentResult.Success -> {
                        sessionData = sessionData.copy(authorization = result.value.authCode)
                        sessionListener?.onSessionFinished(
                            SessionResult.Success(AccountSessionResult(
                                sessionData.authorization!!,
                                sessionData.selectedBank,
                                sessionData.linkingType!!
                            ))
                        )
                    }
                    is FragmentResult.Canceled -> sessionListener?.onSessionFinished(SessionResult.Canceled)
                    is FragmentResult.Failure -> sessionListener?.onSessionFinished(SessionResult.Failure(result.error))
                }
            }
        }
    }
}