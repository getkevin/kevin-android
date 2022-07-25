package eu.kevin.accounts.accountlinking

import eu.kevin.common.architecture.interfaces.IEvent

internal sealed class AccountLinkingEvent : IEvent {
    data class LoadWebPage(val url: String) : AccountLinkingEvent()
}