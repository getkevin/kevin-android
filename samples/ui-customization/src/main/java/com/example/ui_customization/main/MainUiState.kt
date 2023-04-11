package com.example.ui_customization.main

internal data class MainUiState(
    val isLoading: Boolean = false,
    val accountLinkingState: String? = null,
    val userMessage: String? = null
)