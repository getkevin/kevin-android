package eu.kevin.accounts.bankselection.usecases

import eu.kevin.accounts.bankselection.entities.SupportedBanksFilter
import eu.kevin.accounts.bankselection.usecases.ValidateBanksConfigUseCase.Status
import eu.kevin.accounts.networking.entities.ApiBank
import eu.kevin.testcore.base.BaseUnitTest
import eu.kevin.testcore.dispatchers.TestCoroutineDispatchers
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ValidateBanksConfigUseCaseTest : BaseUnitTest() {

    private val getSupportedBanksUseCase = mockk<GetSupportedBanksUseCase>(relaxed = true)
    private lateinit var useCase: ValidateBanksConfigUseCase

    private val testBank1 = ApiBank("SWEDBANK_LT", "Swedbank", "Swedbank", "LT", false, "", "HABALT22", false, true)
    private val testBank2 = ApiBank("REVOLUT_LT", "Revolut", "Revolut", "LT", false, "", "REVOLT21", false, false)

    @Before
    override fun setUp() {
        super.setUp()

        coEvery { getSupportedBanksUseCase.getSupportedBanks(any(), any(), any()) } returns listOf(testBank1, testBank2)

        useCase = ValidateBanksConfigUseCase(
            dispatchers = TestCoroutineDispatchers,
            getSupportedBanksUseCase = getSupportedBanksUseCase
        )
    }

    @Test
    fun `Should return Valid status if banks config not provided`() = testCoroutineScope.runTest {
        val result = useCase.validateBanksConfig(
            authState = "state",
            country = null,
            preselectedBank = null,
            supportedBanksFilter = SupportedBanksFilter()
        )

        assertEquals(Status.Valid(null), result)
    }

    @Test
    fun `Should return Valid status if at least one provided filter is supported`() = testCoroutineScope.runTest {
        coEvery { getSupportedBanksUseCase.getSupportedBanks(any(), any(), any()) } returns listOf(testBank2)

        val result = useCase.validateBanksConfig(
            authState = "state",
            country = null,
            preselectedBank = null,
            supportedBanksFilter = SupportedBanksFilter(listOf("REVOLUT_LT", "ABC"))
        )

        assertEquals(Status.Valid(null), result)
    }

    @Test
    fun `Should return Valid status if preselected bank is supported`() = testCoroutineScope.runTest {
        val result = useCase.validateBanksConfig(
            authState = "state",
            country = null,
            preselectedBank = "REVOLUT_LT",
            supportedBanksFilter = SupportedBanksFilter()
        )

        assertEquals(Status.Valid(testBank2), result)
    }

    @Test
    fun `Should return FiltersInvalid status if provided filters are not supported`() = testCoroutineScope.runTest {
        coEvery { getSupportedBanksUseCase.getSupportedBanks(any(), any(), any()) } returns emptyList()

        val result = useCase.validateBanksConfig(
            authState = "state",
            country = null,
            preselectedBank = null,
            supportedBanksFilter = SupportedBanksFilter(listOf("ABC"))
        )

        assertEquals(Status.FiltersInvalid, result)
    }

    @Test
    fun `Should return PreselectedInvalid status if preselected bank is not supported`() = testCoroutineScope.runTest {
        val result = useCase.validateBanksConfig(
            authState = "state",
            country = null,
            preselectedBank = "ABC",
            supportedBanksFilter = SupportedBanksFilter()
        )

        assertEquals(Status.PreselectedInvalid, result)
    }

    @Test
    fun `Should return PreselectedInvalid status if preselected bank is not present in supported filters`() =
        testCoroutineScope.runTest {
            coEvery { getSupportedBanksUseCase.getSupportedBanks(any(), any(), any()) } returns listOf(testBank1)

            val result = useCase.validateBanksConfig(
                authState = "state",
                country = null,
                preselectedBank = "REVOLUT_LT",
                supportedBanksFilter = SupportedBanksFilter(listOf("SWEDBANK_LT"))
            )

            assertEquals(Status.PreselectedInvalid, result)
        }
}