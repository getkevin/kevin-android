package eu.kevin.accounts.bankselection.usecases

import eu.kevin.accounts.bankselection.entities.SupportedBanksFilter
import eu.kevin.accounts.bankselection.managers.BankTestManager
import eu.kevin.testcore.base.BaseUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetSupportedBanksUseCaseTest : BaseUnitTest() {

    private lateinit var useCase: GetSupportedBanksUseCase

    @Before
    override fun setUp() {
        super.setUp()

        useCase = GetSupportedBanksUseCase(BankTestManager())
    }

    @Test
    fun `Should return all banks if there is no filters`() = testCoroutineScope.runTest {
        val resultIds = useCase.getSupportedBanks("lv", "state")
            .map { it.id }
            .toSet()

        val expectedIds =
            setOf("REVOLUT_LV", "SWEDBANK_LV", "SEB_LV", "LUMINOR_LV", "CITADELE_LV", "INDUSTRA_LV", "SIGNETBANK_LV")

        assertEquals(expectedIds, resultIds)
    }

    @Test
    fun `Should filter banks by name properly`() = testCoroutineScope.runTest {
        val resultIds = useCase.getSupportedBanks(
            country = "lv",
            authState = "state",
            supportedBanksFilter = SupportedBanksFilter(listOf("swedbank_lv", "revolut_lv"))
        ).map { it.id }.toSet()

        val expectedIds = setOf("SWEDBANK_LV", "REVOLUT_LV")

        assertEquals(expectedIds, resultIds)
    }

    @Test
    fun `Should filter banks that support account linking`() = testCoroutineScope.runTest {
        val resultIds = useCase.getSupportedBanks(
            country = "lv",
            authState = "state",
            supportedBanksFilter = SupportedBanksFilter(showOnlyAccountLinkingSupportedBanks = true)
        ).map { it.id }.toSet()

        val expectedIds = setOf("REVOLUT_LV", "SWEDBANK_LV", "SEB_LV")

        assertEquals(expectedIds, resultIds)
    }
}