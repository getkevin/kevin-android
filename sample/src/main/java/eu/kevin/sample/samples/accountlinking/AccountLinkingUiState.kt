package eu.kevin.sample.samples.accountlinking

internal data class AccountLinkingUiState(
    val isLoading: Boolean = false,
    val accountLinkingState: String? = null,
    val userMessage: String? = null
)