package eu.kevin.accounts.linkingsession

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.accounts.accountlinking.AccountLinkingFragment
import eu.kevin.accounts.accountlinking.AccountLinkingFragmentConfiguration
import eu.kevin.accounts.linkingsession.entities.AccountLinkingConfiguration
import eu.kevin.accounts.linkingsession.entities.AccountLinkingSessionData
import eu.kevin.accounts.linkingsession.enums.AccountLinkingFlowItem
import eu.kevin.accounts.linkingsession.enums.AccountLinkingFlowItem.*
import eu.kevin.accounts.bankselection.BankSelectionFragment
import eu.kevin.accounts.bankselection.BankSelectionFragmentConfiguration
import eu.kevin.accounts.bankselection.entities.Bank
import eu.kevin.accounts.networking.AccountsClientProvider
import eu.kevin.core.architecture.BaseFlowSession
import eu.kevin.core.architecture.routing.GlobalRouter
import eu.kevin.core.entities.ActivityResult
import eu.kevin.core.extensions.setFragmentResultListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class AccountLinkingSession(
    private val fragmentManager: FragmentManager,
    private val configuration: AccountLinkingConfiguration,
    private val lifecycleOwner: LifecycleOwner,
    registryOwner: SavedStateRegistryOwner
) : BaseFlowSession(lifecycleOwner, registryOwner), LifecycleObserver {

    private var sessionListener: AccountLinkingSessionListener? = null

    private val backStackListener = FragmentManager.OnBackStackChangedListener {
        currentFlowIndex = fragmentManager.backStackEntryCount - 1
    }

    private val flowItems = mutableListOf<AccountLinkingFlowItem>()
    private val accountsClient = AccountsClientProvider.kevinAccountsClient
    private var currentFlowIndex by savedState(-1)
    private var sessionData by savedState(AccountLinkingSessionData())

    init {
        lifecycleOwner.lifecycle.addObserver(this)
        fragmentManager.addOnBackStackChangedListener(backStackListener)
        initFragmentResultListeners()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        sessionListener = null
    }

    fun beginFlow(listener: AccountLinkingSessionListener?) {
        sessionListener = listener

        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val selectedBank = if (configuration.preselectedBank != null) {
                withContext(Dispatchers.Main) {
                    sessionListener?.showLoading(true)
                }
                val apiBanks = accountsClient.getSupportedBanks(configuration.state, configuration.preselectedCountry)
                apiBanks.data.firstOrNull { it.id == configuration.preselectedBank }?.let {
                    Bank(it.id, it.name, it.officialName, it.imageUri, it.bic)
                }
            } else {
                null
            }

            withContext(Dispatchers.Main) {
                sessionListener?.showLoading(false)
                if (currentFlowIndex == -1) {
                    sessionData = sessionData.copy(
                        selectedCountry = configuration.preselectedCountry,
                        selectedBank = selectedBank
                    )
                }
                initializeFlow()
                if (currentFlowIndex == -1) {
                    GlobalRouter.pushFragment(getFlowFragment(0))
                }
            }
        }
    }

    private fun initializeFlow() {
        val flow = mutableListOf<AccountLinkingFlowItem>()
        if (!configuration.skipBankSelection) {
            flow.add(BANK_SELECTION)
        }
        flow.add(LINK_ACCOUNT_WEB_VIEW)
        flowItems.clear()
        flowItems.addAll(flow)
    }

    private fun handleFowNavigation() {
        if (flowItems.size == currentFlowIndex) {
            sessionListener?.onSessionFinished(
                ActivityResult.Success(AccountLinkingResult(
                    sessionData.authorization!!,
                    sessionData.selectedBank!!
                ))
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
                        sessionData.selectedBank?.id,
                        configuration.state
                    )
                }
            }
            LINK_ACCOUNT_WEB_VIEW -> {
                AccountLinkingFragment().also {
                    it.configuration = AccountLinkingFragmentConfiguration(
                        configuration.state,
                        sessionData.selectedBank?.id!!
                    )
                }
            }
        }
    }

    private fun initFragmentResultListeners() {
        with(fragmentManager) {
            setFragmentResultListener(BankSelectionFragment.Contract, lifecycleOwner) { selectedBank ->
                sessionData = sessionData.copy(selectedBank = selectedBank)
                currentFlowIndex++
                handleFowNavigation()
            }
            setFragmentResultListener(AccountLinkingFragment.Contract, lifecycleOwner) { result ->
                sessionData = sessionData.copy(authorization = result.authCode)
                currentFlowIndex++
                handleFowNavigation()
            }
        }
    }
}