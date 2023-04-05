package com.example.account_linking.main

internal data class MainUiState(
    val isLoading: Boolean = false,
    val accountLinkingState: String? = null,
    val userMessage: String? = null
)