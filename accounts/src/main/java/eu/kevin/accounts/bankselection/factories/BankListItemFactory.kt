package eu.kevin.accounts.bankselection.factories

import eu.kevin.accounts.bankselection.entities.BankListItem
import eu.kevin.accounts.networking.entities.ApiBank

internal object BankListItemFactory {
    fun getBankList(apiBanks: List<ApiBank>, selectedBankId: String? = null): List<BankListItem> {
        val banks = apiBanks.sortedBy { it.officialName }.map {
            BankListItem(it.id, it.name, it.imageUri)
        }
        val selectedBank = banks.firstOrNull { it.bankId == selectedBankId }
        if (selectedBank != null) {
            banks.firstOrNull { it.bankId == selectedBankId }?.isSelected = true
        } else {
            banks.firstOrNull()?.isSelected = true
        }
        return banks
    }
}