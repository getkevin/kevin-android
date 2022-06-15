package eu.kevin.demo.screens.accountlinking

import eu.kevin.demo.data.database.entities.LinkedAccount

internal data class AccountLinkingState(
    val isLoading: Boolean = false,
    val linkedAccounts: List<LinkedAccount> = emptyList()
)