package eu.kevin.demo.screens.accountlinking

internal sealed class AccountLinkingAction {
    data class OpenAccountLinkingSession(val state: String) : AccountLinkingAction()
}