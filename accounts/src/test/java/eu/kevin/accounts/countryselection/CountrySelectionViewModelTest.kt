package eu.kevin.accounts.countryselection

import eu.kevin.accounts.countryselection.entities.Country
import eu.kevin.accounts.countryselection.managers.CountriesTestManager
import eu.kevin.accounts.countryselection.usecases.SupportedCountryUseCase
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.entities.LoadingState
import eu.kevin.testcore.base.BaseViewModelTest
import eu.kevin.testcore.dispatchers.TestCoroutineDispatchers
import eu.kevin.testcore.extensions.updateInternalState
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CountrySelectionViewModelTest : BaseViewModelTest() {

    private lateinit var viewModel: CountrySelectionViewModel

    @Before
    override fun setUp() {
        super.setUp()
        viewModel = CountrySelectionViewModel(
            SupportedCountryUseCase(CountriesTestManager()),
            TestCoroutineDispatchers,
            savedStateHandle
        )
        every { savedStateHandle.get<Any>(any()) } returns null
    }

    @Test
    fun `test handleIntent() Initialize without preselected country`() = testCoroutineScope.runTest {
        val config = CountrySelectionFragmentConfiguration(
            null,
            emptyList(),
            ""
        )

        val states = mutableListOf<CountrySelectionState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        viewModel.intents.trySend(CountrySelectionIntent.Initialize(config))

        assertEquals(3, states.size)
        assertEquals(LoadingState.Loading(true), states[1].loadingState)
        assertEquals(LoadingState.Loading(false), states[2].loadingState)
        assertTrue(states[2].supportedCountries.isNotEmpty())
        job.cancel()
    }

    @Test
    fun `test handleIntent() Initialize with preselected country`() = testCoroutineScope.runTest {
        val selectedCountryIso = "lt"
        val config = CountrySelectionFragmentConfiguration(
            selectedCountryIso,
            emptyList(),
            ""
        )

        val states = mutableListOf<CountrySelectionState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        viewModel.intents.trySend(CountrySelectionIntent.Initialize(config))

        assertEquals(3, states.size)
        assertEquals(LoadingState.Loading(true), states[1].loadingState)
        assertEquals(LoadingState.Loading(false), states[2].loadingState)
        assertTrue(states[2].supportedCountries.isNotEmpty())
        assertTrue(states[2].supportedCountries.firstOrNull { it.isSelected }?.iso == selectedCountryIso)
        job.cancel()
    }

    @Test
    fun `test handleIntent() HandleCountrySelection`() = testCoroutineScope.runTest {
        viewModel.updateInternalState(
            CountrySelectionState(
                CountriesTestManager().getSupportedCountries("").map {
                    Country(it)
                }
            )
        )
        val selectedCountryIso = "lt"
        mockkObject(GlobalRouter)
        viewModel.intents.trySend(CountrySelectionIntent.HandleCountrySelection(selectedCountryIso))
        verify(exactly = 1) { GlobalRouter.returnFragmentResult(CountrySelectionContract, selectedCountryIso) }
    }
}