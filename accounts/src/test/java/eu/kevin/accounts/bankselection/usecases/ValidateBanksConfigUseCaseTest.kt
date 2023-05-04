package eu.kevin.accounts.bankselection.usecases

import eu.kevin.accounts.bankselection.usecases.ValidateBanksConfigUseCase.Status
import eu.kevin.accounts.networking.KevinAccountsClient
import eu.kevin.accounts.networking.entities.ApiBank
import eu.kevin.core.networking.entities.KevinResponse
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

    private val accountsClient = mockk<KevinAccountsClient>(relaxed = true)
    private lateinit var useCase: ValidateBanksConfigUseCase

    private val testBank1 = ApiBank("SWEDBANK_LT", "Swedbank", "Swedbank", "LT", false, "", "HABALT22", false, true)
    private val testBank2 = ApiBank("REVOLUT_LT", "Revolut", "Revolut", "LT", false, "", "REVOLT21", false, false)

    @Before
    override fun setUp() {
        super.setUp()

        coEvery { accountsClient.getSupportedBanks(any(), any()) } returns KevinResponse(listOf(testBank1, testBank2))

        useCase = ValidateBanksConfigUseCase(
            dispatchers = TestCoroutineDispatchers,
            accountsClient = accountsClient
        )
    }

    @Test
    fun `Should return Valid status if banks config not provided`() = testCoroutineScope.runTest {
        val result = useCase.validateBanksConfig(
            authState = "state",
            country = null,
            preselectedBank = null,
            banksFilter = emptyList(),
            requireAccountLinkingSupport = false
        )

        assertEquals(Status.Valid(null), result)
    }

    @Test
    fun `Should return Valid status if at least one provided filter is supported`() = testCoroutineScope.runTest {
        val result = useCase.validateBanksConfig(
            authState = "state",
            country = null,
            preselectedBank = null,
            banksFilter = listOf("REVOLUT_LT", "ABC"),
            requireAccountLinkingSupport = false
        )

        assertEquals(Status.Valid(null), result)
    }

    @Test
    fun `Should return Valid status if preselected bank is supported`() = testCoroutineScope.runTest {
        val result = useCase.validateBanksConfig(
            authState = "state",
            country = null,
            preselectedBank = "REVOLUT_LT",
            banksFilter = emptyList(),
            requireAccountLinkingSupport = false
        )

        assertEquals(Status.Valid(testBank2), result)
    }

    @Test
    fun `Should return FiltersInvalid status if provided filters are not supported`() = testCoroutineScope.runTest {
        val result = useCase.validateBanksConfig(
            authState = "state",
            country = null,
            preselectedBank = null,
            banksFilter = listOf("ABC", "ZXC"),
            requireAccountLinkingSupport = false
        )

        assertEquals(Status.FiltersInvalid, result)
    }

    @Test
    fun `Should return PreselectedInvalid status if preselected bank is not supported`() = testCoroutineScope.runTest {
        val result = useCase.validateBanksConfig(
            authState = "state",
            country = null,
            preselectedBank = "ABC",
            banksFilter = emptyList(),
            requireAccountLinkingSupport = false
        )

        assertEquals(Status.PreselectedInvalid, result)
    }

    @Test
    fun `Should return PreselectedInvalid status if preselected bank is not present in supported filters`() =
        testCoroutineScope.runTest {
            val result = useCase.validateBanksConfig(
                authState = "state",
                country = null,
                preselectedBank = "REVOLUT_LT",
                banksFilter = listOf("SWEDBANK_LT"),
                requireAccountLinkingSupport = false
            )

            assertEquals(Status.PreselectedInvalid, result)
        }

    @Test
    fun `Should filter out banks without account linking support`() = testCoroutineScope.runTest {
        val result = useCase.validateBanksConfig(
            authState = "state",
            country = null,
            preselectedBank = "REVOLUT_LT",
            banksFilter = emptyList(),
            requireAccountLinkingSupport = true
        )

        assertEquals(Status.PreselectedInvalid, result)
    }
}