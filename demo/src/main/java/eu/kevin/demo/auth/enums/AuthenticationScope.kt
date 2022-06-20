package eu.kevin.demo.auth.enums

internal enum class AuthenticationScope(val value: String) {
    PAYMENTS("payments"),
    PAYMENTS_POS("payments_pos"),
    ACCOUNT_DETAILS("accounts_details"),
    ACCOUNT_BALANCES("accounts_balances"),
    ACCOUNT_TRANSACTIONS("accounts_transactions"),
    ACCOUNT_BASIC("accounts_basic");
}