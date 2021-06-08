# kevin. Android SDK

> Android integration implementing kevin. account linking and bank/card in-app payments.

## Prerequisites

- Android minSdkVersion 21

## Getting Started
1. Import library features you will use:

```
implementation 'com.github.getkevin:kevin-core-android:xxx'
implementation 'com.github.getkevin:kevin-accounts-android:xxx'
implementation 'com.github.getkevin:kevin-in-app-payments-android:xxx'
```
2. Initialize plugins you will use in your Application file:

```
Kevin.addPlugin(KevinAccountsPlugin)
Kevin.addPlugin(KevinPaymentsPlugin)
Kevin.configure(
    KevinConfiguration
        .builder()
        .setTheme(R.style.KevinTheme)
        .setHost("your.kevin.host")
        .build()
)
```
## Account Linking
1. Use registerForActivityResult with our ActivityResultContract:

```
val linkAccount = registerForActivityResult(LinkAccountContract()) { result ->
    when (result) {
        is ActivityResult.Success -> {
            //  get account token by calling result.value.linkToken
        }
        is ActivityResult.Canceled -> {
            //   do something on user cancellation
        }
        is ActivityResult.Failure -> {
            //   do something on failure
        }
    }
}
```
2. Customize flow by tweaking our configuration and launch the flow:

```
val config = AccountLinkingConfiguration.Builder(state)
    .setPreselectedCountry("LT")
    .setPreselectedBank("SOME_BANK")
    .setSkipBankSelection(false)
    .build()
linkAccount.launch(config)
```
## In-App Payments
1. Use registerForActivityResult with our ActivityResultContract:

```
val makePayment = registerForActivityResult(PaymentSessionContract()) { result ->
    when (result) {
        is ActivityResult.Success -> {
            //  get payment id by calling result.value.paymentId
        }
        is ActivityResult.Canceled -> {
            //   do something on user cancellation
        }
        is ActivityResult.Failure -> {
            //   do something on failure
        }
    }
}
```
2. Customize flow by tweaking our configuration and launch the flow:

```
val config = PaymentSessionConfiguration.Builder(payment.id, PaymentType.BANK)
    .setPreselectedCountry("LT")
    .setPreselectedBank("SOME_BANK")
    .setSkipBankSelection(false)
    .build()
linkAccount.launch(config)
```
## Examples

The ./sample folder contains a project showing how kevin. can be used.
