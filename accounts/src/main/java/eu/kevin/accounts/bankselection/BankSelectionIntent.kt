package eu.kevin.accounts.bankselection

import eu.kevin.common.architecture.interfaces.IIntent

internal sealed class BankSelectionIntent : IIntent {
    data class Initialize(val configuration: BankSelectionFragmentConfiguration) : BankSelectionIntent()
    data class HandleCountrySelectionClick(
        val configuration: BankSelectionFragmentConfiguration
    ) : BankSelectionIntent()

    data class HandleCountrySelected(
        val selectedCountry: String,
        val configuration: BankSelectionFragmentConfiguration
    ) : BankSelectionIntent()

    data object HandleContinueClicked : BankSelectionIntent()
    data object HandleBackClicked : BankSelectionIntent()
    data class HandleBankSelection(val bankId: String) : BankSelectionIntent()
}