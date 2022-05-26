package eu.kevin.accounts.bankselection

internal interface BankSelectionViewDelegate {
    fun onBackClicked()
    fun onSelectCountryClicked()
    fun onBankClicked(bankId: String)
    fun onContinueClicked()
    fun onTermsAndConditionsClicked()
    fun onPrivacyPolicyClicked()
}