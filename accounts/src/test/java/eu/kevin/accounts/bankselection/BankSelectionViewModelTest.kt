package eu.kevin.accounts.bankselection

import android.os.Bundle
import eu.kevin.accounts.bankselection.entities.Bank
import eu.kevin.accounts.bankselection.factories.BankListItemFactory
import eu.kevin.accounts.bankselection.managers.BankTestManager
import eu.kevin.accounts.countryselection.managers.CountriesTestManager
import eu.kevin.accounts.countryselection.usecases.SupportedCountryUseCase
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.entities.LoadingState
import eu.kevin.testcore.base.BaseViewModelTest
import eu.kevin.testcore.extensions.updateInternalState
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class BankSelectionViewModelTest : BaseViewModelTest() {

    private lateinit var viewModel: BankSelectionViewModel

    @Before
    override fun setUp() {
        super.setUp()
        viewModel = BankSelectionViewModel(
            SupportedCountryUseCase(CountriesTestManager()),
            BankTestManager(),
            testCoroutineDispatcher,
            savedStateHandle
        )
        every { savedStateHandle.get<Any>(any()) } returns null
    }

    @Test
    fun `test handleIntent() Initialize without preselected bank`() = mainCoroutineRule.runBlockingTest {
        val selectedCountry = "lt"
        val config = BankSelectionFragmentConfiguration(
            selectedCountry,
            false,
            emptyList(),
            null,
            ""
        )

        val states = mutableListOf<BankSelectionState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        viewModel.intents.trySend(BankSelectionIntent.Initialize(config))

        Assert.assertEquals(states.size, 3)
        Assert.assertEquals(states[1].loadingState, LoadingState.Loading(true))
        Assert.assertEquals(states[2].loadingState, LoadingState.Loading(false))
        Assert.assertTrue(states[2].bankListItems.isNotEmpty())
        Assert.assertEquals(states[2].isCountrySelectionDisabled, false)
        job.cancel()
    }

    @Test
    fun `test handleIntent() Initialize with preselected bank`() = mainCoroutineRule.runBlockingTest {
        val preselectedBank = "SWEDBANK_LT"
        val selectedCountry = "lt"
        val config = BankSelectionFragmentConfiguration(
            selectedCountry,
            false,
            emptyList(),
            preselectedBank,
            ""
        )

        val states = mutableListOf<BankSelectionState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        viewModel.intents.trySend(BankSelectionIntent.Initialize(config))

        Assert.assertEquals(states.size, 3)
        Assert.assertEquals(states[1].loadingState, LoadingState.Loading(true))
        Assert.assertEquals(states[2].loadingState, LoadingState.Loading(false))
        Assert.assertTrue(states[2].bankListItems.isNotEmpty())
        Assert.assertTrue(states[2].bankListItems.firstOrNull { it.isSelected }?.bankId == preselectedBank)
        Assert.assertEquals(states[2].isCountrySelectionDisabled, false)
        job.cancel()
    }

    @Test
    fun `test handleIntent() Initialize with preselected bank and incorrect country`() = mainCoroutineRule.runBlockingTest {
        val preselectedBank = "SWEDBANK_LT"
        val selectedCountry = "lv"
        val config = BankSelectionFragmentConfiguration(
            selectedCountry,
            false,
            emptyList(),
            preselectedBank,
            ""
        )

        val states = mutableListOf<BankSelectionState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        viewModel.intents.trySend(BankSelectionIntent.Initialize(config))

        Assert.assertEquals(states.size, 3)
        Assert.assertEquals(states[1].loadingState, LoadingState.Loading(true))
        Assert.assertEquals(states[2].loadingState, LoadingState.Loading(false))
        Assert.assertTrue(states[2].bankListItems.isNotEmpty())
        Assert.assertTrue(states[2].bankListItems.firstOrNull { it.isSelected }?.bankId != preselectedBank)
        Assert.assertEquals(states[2].isCountrySelectionDisabled, false)
        job.cancel()
    }

    @Test
    fun `test handleIntent() HandleBackClicked while loading`() = mainCoroutineRule.runBlockingTest {
        viewModel.updateInternalState(BankSelectionState(loadingState = LoadingState.Loading(true)))
        mockkObject(GlobalRouter)
        viewModel.intents.trySend(BankSelectionIntent.HandleBackClicked)
        verify(exactly = 0) { GlobalRouter.popCurrentFragment() }
    }

    @Test
    fun `test handleIntent() HandleBackClicked while not loading`() = mainCoroutineRule.runBlockingTest {
        viewModel.updateInternalState(BankSelectionState(loadingState = LoadingState.Loading(false)))
        mockkObject(GlobalRouter)
        viewModel.intents.trySend(BankSelectionIntent.HandleBackClicked)
        verify(exactly = 1) { GlobalRouter.popCurrentFragment() }
    }

    @Test
    fun `test handleIntent() HandleBankSelection()`() = mainCoroutineRule.runBlockingTest {
        val preselectedBank = "SWEDBANK_LT"
        viewModel.updateInternalState(BankSelectionState(
            bankListItems = BankListItemFactory.getBankList(BankTestManager().getSupportedBanks("lt", ""), null)
        ))
        val states = mutableListOf<BankSelectionState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        viewModel.intents.trySend(BankSelectionIntent.HandleBankSelection(preselectedBank))

        Assert.assertTrue(states[0].bankListItems.firstOrNull { it.isSelected }?.bankId != preselectedBank)

        job.cancel()
    }

    @Test
    fun `test handleIntent() handleCountrySelectionClick()`() = mainCoroutineRule.runBlockingTest {
        val selectedCountry = "lt"
        val config = BankSelectionFragmentConfiguration(
            selectedCountry,
            false,
            emptyList(),
            null,
            ""
        )
        mockkConstructor(Bundle::class)
        every { anyConstructed<Bundle>().putParcelable(any(), any()) } returns Unit
        viewModel.updateInternalState(BankSelectionState(
            selectedCountry = selectedCountry
        ))
        mockkObject(GlobalRouter)
        viewModel.intents.trySend(BankSelectionIntent.HandleCountrySelectionClick(config))
        verify(exactly = 1) { GlobalRouter.pushModalFragment(any()) }
    }

    @Test
    fun `test handleIntent() handleCountrySelected`() = mainCoroutineRule.runBlockingTest {
        val selectedCountry = "lv"
        val config = BankSelectionFragmentConfiguration(
            null,
            false,
            emptyList(),
            null,
            ""
        )

        val states = mutableListOf<BankSelectionState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        viewModel.intents.trySend(BankSelectionIntent.HandleCountrySelected(selectedCountry, config))

        Assert.assertEquals(states.size, 3)
        Assert.assertEquals(states[1].loadingState, LoadingState.Loading(true))
        Assert.assertEquals(states[2].loadingState, LoadingState.Loading(false))
        Assert.assertTrue(states[2].bankListItems.isNotEmpty())
        Assert.assertEquals(states[2].selectedCountry, selectedCountry)
        job.cancel()
    }

    @Test
    fun `test handleIntent() handleContinueClicked`() = mainCoroutineRule.runBlockingTest {
        mockkObject(GlobalRouter)
        val config = BankSelectionFragmentConfiguration(
            "lt",
            false,
            emptyList(),
            "SWEDBANK_LT",
            ""
        )
        viewModel.intents.trySend(BankSelectionIntent.Initialize(config))
        viewModel.intents.trySend(BankSelectionIntent.HandleContinueClicked)
        verify(exactly = 1) {
            GlobalRouter.returnFragmentResult(
                BankSelectionFragment.Contract,
                Bank("SWEDBANK_LT", "Swedbank", "Swedbank AB", "", "HABALT22")
            )
        }
    }
}