package eu.kevin.accounts.countryselection.managers

class CountriesTestManager : CountriesManagerInterface {

    override suspend fun getSupportedCountries(authState: String): List<String> {
        return listOf(
            "at", "be", "bg", "cy", "cz", "de", "dk", "ee", "es", "fi", "fr", "gb", "hr", "hu",
            "ie", "it", "lt", "lu", "lv", "mt", "nl", "no", "pl", "pt", "ro", "se", "si", "sk"
        )
    }
}