package eu.kevin.accounts.bankselection

import android.os.Bundle
import eu.kevin.accounts.bankselection.entities.Bank
import eu.kevin.accounts.bankselection.factories.BankListItemFactory
import eu.kevin.accounts.bankselection.managers.BankTestManager
import eu.kevin.accounts.bankselection.providers.DefaultCountryIsoProvider
import eu.kevin.accounts.bankselection.usecases.GetSupportedBanksUseCase
import eu.kevin.accounts.countryselection.managers.CountriesTestManager
import eu.kevin.accounts.countryselection.usecases.SupportedCountryUseCase
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.entities.LoadingState
import eu.kevin.common.fragment.FragmentResult
import eu.kevin.testcore.base.BaseViewModelTest
import eu.kevin.testcore.dispatchers.TestCoroutineDispatchers
import eu.kevin.testcore.extensions.updateInternalState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class BankSelectionViewModelTest : BaseViewModelTest() {

    private val defaultCountryIsoProvider = mockk<DefaultCountryIsoProvider>()
    private val countriesTestManager = spyk<CountriesTestManager>()

    private lateinit var viewModel: BankSelectionViewModel

    @Before
    override fun setUp() {
        super.setUp()
        viewModel = BankSelectionViewModel(
            defaultCountryIsoProvider = defaultCountryIsoProvider,
            countryUseCase = SupportedCountryUseCase(countriesTestManager),
            banksUseCase = GetSupportedBanksUseCase(BankTestManager()),
            dispatchers = TestCoroutineDispatchers,
            savedStateHandle = savedStateHandle
        )
        every { savedStateHandle.get<Any>(any()) } returns null
    }

    @Test
    fun `test handleIntent() Initialize without preselected bank`() = testCoroutineScope.runTest {
        val selectedCountry = "lt"
        val config = BankSelectionFragmentConfiguration(
            selectedCountry,
            false,
            emptyList(),
            emptyList(),
            "",
            "",
            true
        )

        val states = mutableListOf<BankSelectionState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        viewModel.intents.trySend(BankSelectionIntent.Initialize(config))

        assertEquals(3, states.size)
        assertEquals(LoadingState.Loading(true), states[1].loadingState)
        assertEquals(LoadingState.Loading(false), states[2].loadingState)
        assertTrue(states[2].bankListItems.isNotEmpty())
        assertEquals(false, states[2].isCountrySelectionDisabled)
        job.cancel()
    }

    @Test
    fun `test handleIntent() Initialize with preselected bank`() = testCoroutineScope.runTest {
        val preselectedBank = "SWEDBANK_LT"
        val selectedCountry = "lt"
        val config = BankSelectionFragmentConfiguration(
            selectedCountry,
            false,
            emptyList(),
            emptyList(),
            preselectedBank,
            "",
            true
        )

        val states = mutableListOf<BankSelectionState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        viewModel.intents.trySend(BankSelectionIntent.Initialize(config))

        assertEquals(3, states.size)
        assertEquals(LoadingState.Loading(true), states[1].loadingState)
        assertEquals(LoadingState.Loading(false), states[2].loadingState)
        assertTrue(states[2].bankListItems.isNotEmpty())
        assertTrue(states[2].bankListItems.firstOrNull { it.isSelected }?.bankId == preselectedBank)
        assertEquals(false, states[2].isCountrySelectionDisabled)
        job.cancel()
    }

    @Test
    fun `test handleIntent() Initialize with preselected bank and incorrect country`() = testCoroutineScope.runTest {
        val preselectedBank = "SWEDBANK_LT"
        val selectedCountry = "lv"
        val config = BankSelectionFragmentConfiguration(
            selectedCountry,
            false,
            emptyList(),
            emptyList(),
            preselectedBank,
            "",
            true
        )

        val states = mutableListOf<BankSelectionState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        viewModel.intents.trySend(BankSelectionIntent.Initialize(config))

        assertEquals(3, states.size)
        assertEquals(LoadingState.Loading(true), states[1].loadingState)
        assertEquals(LoadingState.Loading(false), states[2].loadingState)
        assertTrue(states[2].bankListItems.isNotEmpty())
        assertTrue(states[2].bankListItems.firstOrNull { it.isSelected }?.bankId != preselectedBank)
        assertEquals(false, states[2].isCountrySelectionDisabled)
        job.cancel()
    }

    @Test
    fun `test handleIntent() Initialize with preselected bank and incorrect auth state`() = testCoroutineScope.runTest {
        val expectedError = Exception()

        coEvery { countriesTestManager.getSupportedCountries("") } throws expectedError
        mockkObject(GlobalRouter)

        val config = BankSelectionFragmentConfiguration(
            "lt",
            false,
            emptyList(),
            emptyList(),
            "SWEDBANK_LT",
            "",
            true
        )
        viewModel.intents.trySend(BankSelectionIntent.Initialize(config))
        verify(exactly = 1) {
            GlobalRouter.returnFragmentResult(
                BankSelectionContract,
                withArg {
                    assertTrue((it is FragmentResult.Failure) && it.error == expectedError)
                }
            )
        }
    }

    @Test
    fun `test handleIntent() Initialize without country`() = testCoroutineScope.runTest {
        every { defaultCountryIsoProvider.getDefaultCountryIso() } returns "lt"

        val config = BankSelectionFragmentConfiguration(
            null,
            false,
            emptyList(),
            emptyList(),
            null,
            "",
            true
        )

        val states = mutableListOf<BankSelectionState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        viewModel.intents.trySend(BankSelectionIntent.Initialize(config))

        verify(exactly = 1) { defaultCountryIsoProvider.getDefaultCountryIso() }

        assertEquals(3, states.size)
        assertEquals(LoadingState.Loading(true), states[1].loadingState)
        assertEquals(LoadingState.Loading(false), states[2].loadingState)
        assertTrue(states[2].bankListItems.isNotEmpty())
        assertEquals(false, states[2].isCountrySelectionDisabled)
        assertEquals("lt", states[2].selectedCountry)
        job.cancel()
    }

    @Test
    fun `test handleIntent() Initialize without country and unsupported default country`() =
        testCoroutineScope.runTest {
            every { defaultCountryIsoProvider.getDefaultCountryIso() } returns "qwe"

            val config = BankSelectionFragmentConfiguration(
                null,
                false,
                emptyList(),
                emptyList(),
                null,
                "",
                true
            )

            val states = mutableListOf<BankSelectionState>()
            val job = launch {
                viewModel.state.toList(states)
            }

            viewModel.intents.trySend(BankSelectionIntent.Initialize(config))

            verify(exactly = 1) { defaultCountryIsoProvider.getDefaultCountryIso() }

            assertEquals(3, states.size)
            assertEquals(LoadingState.Loading(true), states[1].loadingState)
            assertEquals(LoadingState.Loading(false), states[2].loadingState)
            assertTrue(states[2].bankListItems.isEmpty())
            assertFalse(states[2].isContinueVisible)
            assertEquals(false, states[2].isCountrySelectionDisabled)
            assertEquals("at", states[2].selectedCountry)
            job.cancel()
        }

    @Test
    fun `test handleIntent() HandleBackClicked while loading`() = testCoroutineScope.runTest {
        viewModel.updateInternalState(BankSelectionState(loadingState = LoadingState.Loading(true)))
        mockkObject(GlobalRouter)
        viewModel.intents.trySend(BankSelectionIntent.HandleBackClicked)
        verify(exactly = 0) { GlobalRouter.popCurrentFragment() }
    }

    @Test
    fun `test handleIntent() HandleBackClicked while not loading`() = testCoroutineScope.runTest {
        viewModel.updateInternalState(BankSelectionState(loadingState = LoadingState.Loading(false)))
        mockkObject(GlobalRouter)
        viewModel.intents.trySend(BankSelectionIntent.HandleBackClicked)
        verify(exactly = 1) { GlobalRouter.popCurrentFragment() }
    }

    @Test
    fun `test handleIntent() HandleBankSelection()`() = testCoroutineScope.runTest {
        val preselectedBank = "SWEDBANK_LT"
        viewModel.updateInternalState(
            BankSelectionState(
                bankListItems = BankListItemFactory.getBankList(BankTestManager().getSupportedBanks("lt", ""), null)
            )
        )
        val states = mutableListOf<BankSelectionState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        viewModel.intents.trySend(BankSelectionIntent.HandleBankSelection(preselectedBank))

        assertTrue(states[0].bankListItems.firstOrNull { it.isSelected }?.bankId != preselectedBank)
        assertTrue(states[0].isContinueVisible)

        job.cancel()
    }

    @Test
    fun `test handleIntent() handleCountrySelectionClick()`() = testCoroutineScope.runTest {
        val selectedCountry = "lt"
        val config = BankSelectionFragmentConfiguration(
            selectedCountry,
            false,
            emptyList(),
            emptyList(),
            null,
            "",
            true
        )
        mockkConstructor(Bundle::class)
        every { anyConstructed<Bundle>().putParcelable(any(), any()) } returns Unit
        viewModel.updateInternalState(
            BankSelectionState(
                selectedCountry = selectedCountry
            )
        )
        mockkObject(GlobalRouter)
        viewModel.intents.trySend(BankSelectionIntent.HandleCountrySelectionClick(config))
        verify(exactly = 1) { GlobalRouter.pushModalFragment(any()) }
    }

    @Test
    fun `test handleIntent() handleCountrySelected`() = testCoroutineScope.runTest {
        val selectedCountry = "lv"
        val config = BankSelectionFragmentConfiguration(
            null,
            false,
            emptyList(),
            emptyList(),
            null,
            "",
            true
        )

        val states = mutableListOf<BankSelectionState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        viewModel.intents.trySend(BankSelectionIntent.HandleCountrySelected(selectedCountry, config))

        assertEquals(3, states.size)
        assertEquals(LoadingState.Loading(true), states[1].loadingState)
        assertEquals(LoadingState.Loading(false), states[2].loadingState)
        assertTrue(states[2].bankListItems.isNotEmpty())
        assertEquals(selectedCountry, states[2].selectedCountry)
        job.cancel()
    }

    @Test
    fun `test handleIntent() handleContinueClicked`() = testCoroutineScope.runTest {
        val expectedResult = Bank("SWEDBANK_LT", "Swedbank", "Swedbank AB", "", "HABALT22")
        mockkObject(GlobalRouter)
        val config = BankSelectionFragmentConfiguration(
            "lt",
            false,
            emptyList(),
            emptyList(),
            "SWEDBANK_LT",
            "",
            true
        )
        viewModel.intents.trySend(BankSelectionIntent.Initialize(config))
        viewModel.intents.trySend(BankSelectionIntent.HandleContinueClicked)
        verify(exactly = 1) {
            GlobalRouter.returnFragmentResult(
                BankSelectionContract,
                withArg {
                    assertTrue((it is FragmentResult.Success) && it.value == expectedResult)
                }
            )
        }
    }
}