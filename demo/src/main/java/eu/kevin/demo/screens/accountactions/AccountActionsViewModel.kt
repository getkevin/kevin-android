package eu.kevin.demo.screens.accountactions

import android.content.Context
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.common.architecture.BaseViewModel
import eu.kevin.demo.data.database.DatabaseProvider
import eu.kevin.demo.data.database.LinkedAccountsDao
import eu.kevin.demo.routing.DemoRouter
import eu.kevin.demo.screens.accountactions.AccountActionsIntent.HandleRemoveAccount
import eu.kevin.demo.screens.accountactions.entities.AccountAction
import eu.kevin.demo.screens.accountactions.enums.AccountActionType
import kotlinx.coroutines.launch

internal class AccountActionsViewModel(
    private val linkedAccountsDao: LinkedAccountsDao,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<AccountActionsState, AccountActionsIntent, Nothing>(savedStateHandle) {

    private var id: Long? = null

    override fun getInitialData() = AccountActionsState()

    override suspend fun handleIntent(intent: AccountActionsIntent) {
        when (intent) {
            is HandleRemoveAccount -> DemoRouter.returnFragmentResult(
                AccountActionsContract,
                AccountAction(
                    id = id!!,
                    accountAction = AccountActionType.REMOVE
                )
            )
        }
    }

    fun initialise(configuration: AccountActionsFragmentConfiguration) {
        id = configuration.id
        viewModelScope.launch {
            val account = linkedAccountsDao.getById(configuration.id) ?: return@launch

            updateState {
                it.copy(
                    bankName = account.bankName
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val context: Context,
        owner: SavedStateRegistryOwner
    ) :
        AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return AccountActionsViewModel(
                DatabaseProvider.getDatabase(context).linkedAccountsDao(),
                handle
            ) as T
        }
    }
}