package eu.kevin.demo.screens.chooseaccount

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
import eu.kevin.demo.screens.chooseaccount.ChooseAccountIntent.OnAccountChosen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class ChooseAccountViewModel(
    private val linkedAccountsDao: LinkedAccountsDao,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<ChooseAccountState, ChooseAccountIntent, Nothing>(savedStateHandle) {

    override fun getInitialData() = ChooseAccountState()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            linkedAccountsDao.getLinkedAccountsFlow().onEach { accounts ->
                updateState {
                    it.copy(
                        accounts = accounts
                    )
                }
            }.launchIn(this)
        }
    }

    override suspend fun handleIntent(intent: ChooseAccountIntent) {
        when (intent) {
            is OnAccountChosen -> {
                DemoRouter.returnFragmentResult(ChooseAccountContract, intent.id)
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
            return ChooseAccountViewModel(
                DatabaseProvider.getDatabase(context).linkedAccountsDao(),
                handle
            ) as T
        }
    }
}