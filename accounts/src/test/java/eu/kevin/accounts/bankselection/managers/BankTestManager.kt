package eu.kevin.accounts.bankselection.managers

import eu.kevin.accounts.networking.entities.ApiBank

class BankTestManager : BankManager {

    override suspend fun getSupportedBanks(country: String, authState: String): List<ApiBank> {
        val banks = listOf(
            ApiBank("SWEDBANK_LT", "Swedbank", "Swedbank AB", "LT", false, "", "HABALT22", false),
            ApiBank("SEB_LT", "SEB", "AB SEB bankas", "LT", false, "", "CBVILT2X", false),
            ApiBank("LUMINOR_LT", "Luminor", "Luminor Bank AS", "LT", false, "", "AGBLLT2X", false),
            ApiBank("CITADELE_LT", "Citadele", "Citadele Bank", "LT", false, "", "INDULT2X", false),
            ApiBank("MEDBANK_LT", "Medicinos Bankas", "UAB Medicinos bankas", "LT", false, "", "MDBALT22", false),
            ApiBank("SB_LT", "Šiaulių bankas", "AB Šiaulių bankas", "LT", false, "", "CBSBLT26", false),
            ApiBank("REVOLUT_LT", "Revolut", "Revolut Payments UAB", "LT", false, "", "REVOLT21", false),
            ApiBank("REVOLUT_LV", "Revolut", "Revolut Ltd", "LV", false, "", "REVOGB2L", false),
            ApiBank("SWEDBANK_LV", "Swedbank", "Swedbank AB\n", "LV", false, "", "HABALV22", false),
            ApiBank("SEB_LV", "SEB", "AS \"SEB banka\"", "LV", false, "", "UNLALV2X", false),
            ApiBank("LUMINOR_LV", "Luminor", "Luminor Bank AS", "LV", false, "", "RIKOLV2X", false),
            ApiBank("CITADELE_LV", "Citadele", "AS Citadele banka", "LV", false, "", "PARXLV22", false),
            ApiBank("INDUSTRA_LV", "Industra", "AS Industra Bank", "LV", false, "", "MULTLV2X", false),
            ApiBank("SIGNETBANK_LV", "Signet", "Signet Bank AS", "LV", false, "", "LLBBLV2X", false),
        )
        return banks.filter { it.countryCode.lowercase() == country.lowercase() }
    }
}