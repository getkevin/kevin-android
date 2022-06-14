package eu.kevin.demo.screens.accountlinking

sealed class AccountLinkingAction {
    data class OpenAccountLinkingSession(val state: String) : AccountLinkingAction()
}