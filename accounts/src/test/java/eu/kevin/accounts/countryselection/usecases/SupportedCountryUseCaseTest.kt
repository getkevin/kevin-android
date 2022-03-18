package eu.kevin.accounts.countryselection.usecases

import eu.kevin.accounts.countryselection.managers.CountriesTestManager
import eu.kevin.core.enums.KevinCountry
import eu.kevin.testcore.base.BaseUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SupportedCountryUseCaseTest : BaseUnitTest() {

    private lateinit var countryUseCase: SupportedCountryUseCase

    @Before
    override fun setUp() {
        super.setUp()
        countryUseCase = SupportedCountryUseCase(CountriesTestManager())
    }

    @Test
    fun `test getSupportedCountries() with filter`() = testCoroutineScope.runTest {
        val filter = listOf(KevinCountry.LITHUANIA)
        val filterIsos = filter.map { it.iso }
        val countries = countryUseCase.getSupportedCountries("", filter)

        countries.forEach {
            assertTrue(filterIsos.contains(it))
        }
    }

    @Test
    fun `test getSupportedCountries() without filter`() = testCoroutineScope.runTest {
        val countries = countryUseCase.getSupportedCountries("", emptyList())
        assertTrue(countries.isNotEmpty())
    }
}