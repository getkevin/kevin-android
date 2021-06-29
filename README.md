# kevin. Android SDK

> Android integration for kevin. account linking and bank/card in-app payments.

## Prerequisites

- Android minSdkVersion 21

## Getting Started
1. Import library features you will use:

```gradle
implementation 'com.github.getkevin.kevin-android:core:xxx'
implementation 'com.github.getkevin.kevin-android:accounts:xxx'
implementation 'com.github.getkevin.kevin-android:in-app-payments:xxx'
```
or import all features all together:
```gradle
implementation 'com.github.getkevin:kevin-android:xxx'
```
2. Initialize plugins you will use in your Application file:

```kotlin
Kevin.setLocale(Locale("en"))   //  optional locale setup (by default phone locale will be used)
Kevin.setTheme(R.style.KevinTheme)  //  supply a mandatory theme
KevinAccountsPlugin.configure(
    KevinAccountsConfiguration.builder()
        .setCallbackUrl("https://your.callback.url")    //  callback is mandatory
        .build()
)
KevinPaymentsPlugin.configure(
    KevinPaymentsConfiguration.builder()
        .setCallbackUrl("https://your.callback.url")    //  callback is mandatory
        .build()
)
```
## Account Linking
1. Use registerForActivityResult with our ActivityResultContract:

```kotlin
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

```kotlin
val config = AccountLinkingConfiguration.Builder(payment.id, paymentType)
    .setPreselectedCountry(KevinCountry.LITHUANIA)  //  optional option to preselect country
    .setCountryFilter(listOf(   //  optional option to supply country list
        KevinCountry.LATVIA,
        KevinCountry.LITHUANIA,
        KevinCountry.ESTONIA
    ))
    .setDisableCountrySelection(false)  //  optional option to disable country selection
    .setPreselectedBank("SOME_BANK_ID") //  optional option to preselect bank
    .setSkipBankSelection(false)    //  optional skip of bank selection (should be used with preselectedBank)
    .build()
linkAccount.launch(config)
```
## In-App Payments
1. Use registerForActivityResult with our ActivityResultContract:

```kotlin
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

```kotlin
val config = PaymentSessionConfiguration.Builder(payment.id, paymentType)
    .setPreselectedCountry(KevinCountry.LITHUANIA)  //  optional option to preselect country
    .setCountryFilter(listOf(   //  optional option to supply country list
        KevinCountry.LATVIA,
        KevinCountry.LITHUANIA,
        KevinCountry.ESTONIA
    ))
    .setDisableCountrySelection(false)  //  optional option to disable country selection
    .setPreselectedBank("SOME_BANK_ID") //  optional option to preselect bank
    .setSkipBankSelection(false)    //  optional skip of bank selection (should be used with preselectedBank)
    .build()
linkAccount.launch(config)
```
## UI customization
Built-in windows can be widely customised. Override kevin. theme and control a wide array of properties:

```xml
<style name="KevinTheme" parent="Theme.Kevin.Base">
    <item name="android:statusBarColor">#FFFFFF</item>
    <item name="android:navigationBarColor">#FFFFFF</item>
    <item name="android:windowLightStatusBar">true</item>

    <item name="kevinToolbarColor">#FFFFFF</item>
    <item name="fontFamily">@font/custom_font</item>

    <item name="kevinPrimaryBackgroundColor">#FFFFFF</item>
    <item name="kevinSecondaryBackgroundColor">#FAFAFA</item>

    <item name="kevinSelectedOnPrimaryColor">#F0F5FC</item>
    <item name="kevinSelectedOnSecondaryColor">#29AFB6BB</item>

    <item name="kevinRippleOnPrimaryColor">#FAFAFA</item>
    <item name="kevinRippleOnSecondaryColor">#FAFAFA</item>

    <item name="kevinErrorTextColor">#FF0000</item>
    <item name="kevinPrimaryTextColor">#1A1A1A</item>
    <item name="kevinSecondaryTextColor">#747E87</item>

    <item name="kevinToolbarStyle">@style/KevinToolbarTheme</item>
    <item name="kevinPrimaryButtonStyle">@style/KevinPrimaryButtonStyle</item>
</style>

<style name="KevinToolbarTheme" parent="Kevin.Toolbar">
    <item name="navigationIcon">@drawable/ic_back</item>
    <item name="android:background">#FFFFFF</item>
    <item name="titleTextColor">#1A1A1A</item>
    <item name="titleTextAppearance">@style/KevinToolTextBarStyle</item>
    <item name="fontFamily">@font/custom_font</item>
</style>

<style name="KevinToolTextBarStyle" parent="TextAppearance.AppCompat.Widget.ActionBar.Title">
    <item name="android:ellipsize">end</item>
    <item name="fontFamily">@font/custom_font</item>
    <item name="android:maxLines">1</item>
    <item name="android:textSize">17sp</item>
    <item name="android:textAlignment">viewStart</item>
    <item name="android:textColor">#1A1A1A</item>
</style>

<style name="KevinPrimaryButtonStyle" parent="Widget.MaterialComponents.Button">
    <item name="android:gravity">center</item>
    <item name="cornerRadius">32dp</item>
    <item name="android:minHeight">64dp</item>
    <item name="android:textAllCaps">false</item>
    <item name="android:layout_width">wrap_content</item>
    <item name="android:maxWidth">340dp</item>
    <item name="android:textSize">14sp</item>
    <item name="android:stateListAnimator">@animator/custom_click_animator</item>
    <item name="android:textColor">#FFFFFF</item>
    <item name="rippleColor">#0B1E42</item>
    <item name="fontFamily">@font/custom_font</item>
    <item name="android:backgroundTint">#0B1E42</item>
    <item name="android:textAppearance">@android:style/TextAppearance.Material.Widget.Button
    </item>
</style>
```

## Examples

The ./demo folder contains a project showing how kevin. can be used.
