package eu.kevin.accounts.bankselection.factories

import eu.kevin.accounts.networking.entities.ApiBank
import eu.kevin.testcore.base.BaseUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi

class BankListItemFactoryTest : BaseUnitTest() {

    @Test
    fun `test getBankList without selected bank id`() {
        val listItems = BankListItemFactory.getBankList(
            listOf(
                ApiBank("SWEDBANK_LT", "Swedbank", "Swedbank AB", "LT", false, "", "HABALT22", false),
                ApiBank("SEB_LT", "SEB", "AB SEB bankas", "LT", false, "", "CBVILT2X", false),
                ApiBank("LUMINOR_LT", "Luminor", "Luminor Bank AS", "LT", false, "", "AGBLLT2X", false),
                ApiBank("CITADELE_LT", "Citadele", "Citadele Bank", "LT", false, "", "INDULT2X", false),
                ApiBank("MEDBANK_LT", "Medicinos Bankas", "UAB Medicinos bankas", "LT", false, "", "MDBALT22", false)
                ),
            null
        )
        assertEquals(listItems.size, 5)
        assertTrue(listItems.filter { it.isSelected }.isNotEmpty())
    }

    @Test
    fun `test getBankList with selected bank id`() {
        val selectedBankId = "LUMINOR_LT"
        val listItems = BankListItemFactory.getBankList(
            listOf(
                ApiBank("SWEDBANK_LT", "Swedbank", "Swedbank AB", "LT", false, "", "HABALT22", false),
                ApiBank("SEB_LT", "SEB", "AB SEB bankas", "LT", false, "", "CBVILT2X", false),
                ApiBank("LUMINOR_LT", "Luminor", "Luminor Bank AS", "LT", false, "", "AGBLLT2X", false),
                ApiBank("CITADELE_LT", "Citadele", "Citadele Bank", "LT", false, "", "INDULT2X", false),
                ApiBank("MEDBANK_LT", "Medicinos Bankas", "UAB Medicinos bankas", "LT", false, "", "MDBALT22", false)
            ),
            selectedBankId
        )
        assertEquals(listItems.size, 5)
        assertTrue(listItems.filter { it.isSelected }.firstOrNull()?.bankId == selectedBankId)
    }

    @Test
    fun `test getBankList with selected bank id that doesn't exist`() {
        val nonExistentBankId = "BANK_ID"
        val listItems = BankListItemFactory.getBankList(
            listOf(
                ApiBank("SWEDBANK_LT", "Swedbank", "Swedbank AB", "LT", false, "", "HABALT22", false),
                ApiBank("SEB_LT", "SEB", "AB SEB bankas", "LT", false, "", "CBVILT2X", false),
                ApiBank("LUMINOR_LT", "Luminor", "Luminor Bank AS", "LT", false, "", "AGBLLT2X", false),
                ApiBank("CITADELE_LT", "Citadele", "Citadele Bank", "LT", false, "", "INDULT2X", false),
                ApiBank("MEDBANK_LT", "Medicinos Bankas", "UAB Medicinos bankas", "LT", false, "", "MDBALT22", false)
            ),
            nonExistentBankId
        )
        assertEquals(listItems.size, 5)
        assertTrue(listItems.filter { it.isSelected }.firstOrNull()?.bankId != nonExistentBankId)
    }
}