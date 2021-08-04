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
val config = AccountLinkingConfiguration.Builder(state)
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
    <item name="android:windowAnimationStyle">?kevinWindowTransitionStyle</item>

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
    <item name="kevinWindowTransitionStyle">@style/KevinWindowTransition</item>
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

<style name="KevinWindowTransition" parent="Kevin.Window.Transition">
    <item name="android:activityOpenEnterAnimation">@anim/slide_in</item>
    <item name="android:activityOpenExitAnimation">@anim/fade_out</item>
    <item name="android:activityCloseEnterAnimation">@anim/fade_in</item>
    <item name="android:activityCloseExitAnimation">@anim/slide_out</item>
</style>
```

You can also customize BottomSheetDialog window. Our SDK supports edge to edge ui, so you can choose
if you want to draw behind system bars or not:

```xml
<style name="KevinTheme" parent="Theme.Kevin.Base">
    <item name="bottomSheetDialogTheme">@style/KevinBottomSheetTheme</item>
</style>

<style name="KevinBottomSheetTheme" parent="Theme.Kevin.BottomSheet.Base">
    <item name="android:windowIsFloating">false</item>
    <item name="enableEdgeToEdge">true</item>
    <item name="android:navigationBarColor">@android:color/transparent</item>
    <item name="paddingBottomSystemWindowInsets">false</item>
    <item name="android:statusBarColor">@android:color/transparent</item>
    <item name="android:windowLightStatusBar">false</item>
</style>
```

## Flutter Android
Our SDK can also be used in Flutter. Setup is very similar to regular android:

1. Add dependencies as shown in ***Getting started*** section
2. Initialize plugins you will use inside your Application class as shown in ***Getting started*** section
3. For theme customization, refer to ***UI customization*** section

### Account linking
1. Create method channel and handle account linking method call inside Flutter activity
```kotlin
class MainActivity : FlutterActivity() {

    private val CHANNEL = "com.startActivity/testChannel"
    private var flutterResult: MethodChannel.Result? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GeneratedPluginRegistrant.registerWith(flutterEngine!!)

        MethodChannel(
            flutterEngine!!.dartExecutor.binaryMessenger, CHANNEL
        ).setMethodCallHandler { call, result ->
            if (call.method.equals("OpenKevinAccountLinking")) {
                flutterResult = result

                val accountLinkingConfiguration = AccountLinkingConfiguration.Builder("state")
                    .setPreselectedCountry(KevinCountry.LITHUANIA)
                    .setDisableCountrySelection(false)
                    .build()
                val intent = Intent(this, AccountLinkingActivity::class.java)
                intent.putExtra(LinkAccountContract.CONFIGURATION_KEY, accountLinkingConfiguration)
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                result.notImplemented()
            }
        }
    }

    companion object {
        const val REQUEST_CODE = 100
    }
}
```
2. Handle activity result inside your Flutter activity
```kotlin
class MainActivity : FlutterActivity() {

    private var flutterResult: MethodChannel.Result? = null
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            val result = data?.getParcelableExtra<ActivityResult<AccountLinkingResult>>(LinkAccountContract.RESULT_KEY)
            when (result) {
                is ActivityResult.Success -> {
                    flutterResult?.success(result.value.linkToken)
                }
                is ActivityResult.Canceled -> {
                    flutterResult?.success("canceled")
                }
                is ActivityResult.Failure -> {
                    flutterResult?.success("failed")
                }
            }
        } else {
            flutterResult?.error("error", "oooops", null)
        }
    }

    companion object {
        const val REQUEST_CODE = 100
    }
}
```
3. Open account linking activity from Flutter and get the result

```dart
static const platform = MethodChannel('com.example/methodChannel');
Future<void> _startAccountLinkingActivity() async {
    try {
      final String result = await platform.invokeMethod('OpenKevinAccountLinking');
      // do something with result
    } on PlatformException catch (e) {
      debugPrint("Error: '${e.message}'.");
    }
  }
```

### In-app payments
1. Create method channel and handle payment method call inside Flutter activity
```kotlin
class MainActivity : FlutterActivity() {

    private val CHANNEL = "com.startActivity/testChannel"
    private var flutterResult: MethodChannel.Result? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GeneratedPluginRegistrant.registerWith(flutterEngine!!)

        MethodChannel(
            flutterEngine!!.dartExecutor.binaryMessenger, CHANNEL
        ).setMethodCallHandler { call, result ->
            if (call.method.equals("OpenKevinPayment")) {
                flutterResult = result

                val paymentSessionConfiguration = PaymentSessionConfiguration.Builder("paymentId", PaymentType.BANK)
                    .setPreselectedCountry(KevinCountry.LITHUANIA)
                    .setSkipBankSelection(false)
                    .build()
                val intent = Intent(this, PaymentSessionActivity::class.java)
                intent.putExtra(PaymentSessionContract.CONFIGURATION_KEY, paymentSessionConfiguration)
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                result.notImplemented()
            }
        }
    }

    companion object {
        const val REQUEST_CODE = 100
    }
}
```
2. Handle activity result inside your Flutter activity
```kotlin
class MainActivity : FlutterActivity() {

    private var flutterResult: MethodChannel.Result? = null
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            val result = data?.getParcelableExtra<ActivityResult<PaymentSessionResult>>(PaymentSessionContract.RESULT_KEY)
            when (result) {
                is ActivityResult.Success -> {
                    flutterResult?.success(result.value.paymentId)
                }
                is ActivityResult.Canceled -> {
                    flutterResult?.success("canceled")
                }
                is ActivityResult.Failure -> {
                    flutterResult?.success("failed")
                }
            }
        } else {
            flutterResult?.error("error", "oooops", null)
        }
    }

    companion object {
        const val REQUEST_CODE = 100
    }
}
```
3. Open payment activity from Flutter and get the result

```dart
static const platform = MethodChannel('com.example/methodChannel');
Future<void> _startPaymentActivity() async {
  try {
    final String result = await platform.invokeMethod('OpenKevinPayment');
    // do something with result
  } on PlatformException catch (e) {
    debugPrint("Error: '${e.message}'.");
  }
}
```

## React Native
Our SDK can also be used in React Native. Setup is very similar to regular android:

1. Add dependencies as shown in ***Getting started*** section to your native Android module
2. Initialize plugins you will use inside your Application class as shown in ***Getting started*** section
3. For theme customization, refer to ***UI customization*** section

### Account linking
1. Create ***KevinModule*** and register it in your ***ReactPackage*** class
2. Create new ***ReactMethod*** in ***KevinModule*** class that will be called from JS side and will open ***AccountLinkingActivity***:
```kotlin
class KevinModule (val context: ReactApplicationContext?) : ReactContextBaseJavaModule(context),
    ActivityEventListener {

    private var callback: Callback? = null

    init {
        context?.addActivityEventListener(this)
    }

    @ReactMethod
    fun linkAccount(callback: Callback) {
        this.callback = callback
        val accountLinkingConfiguration = AccountLinkingConfiguration.Builder("state")
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
        val intent = Intent(currentActivity, AccountLinkingActivity::class.java)
        intent.putExtra(LinkAccountContract.CONFIGURATION_KEY, accountLinkingConfiguration)

        context?.startActivityForResult(intent, REQUEST_CODE, null)
    }

    companion object {
        const val REQUEST_CODE = 1000
    }
}
```
3. Handle activity result inside ***KevinModule***:
```kotlin
class KevinModule (val context: ReactApplicationContext?) : ReactContextBaseJavaModule(context),
    ActivityEventListener {

    private var callback: Callback? = null
    
    override fun onActivityResult(
        activity: Activity?,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == REQUEST_CODE) {
            val result = data?.getParcelableExtra<ActivityResult<AccountLinkingResult>>(LinkAccountContract.RESULT_KEY)
            when (result) {
                is ActivityResult.Success -> {
                    callback?.invoke(result.value.linkToken)
                }
                is ActivityResult.Canceled -> {
                    callback?.invoke("Canceled")
                }
                is ActivityResult.Failure -> {
                    callback?.invoke("Failed")
                }
            }
        }
    }

    companion object {
        const val REQUEST_CODE = 1000
    }
}
```
4. Call newly created ***ReactMethod*** from JS and handle received result:
```js
    import { NativeModules } from 'react-native';
    const { KevinModule } = NativeModules;
    
    KevinModule.linkAccount(result => {
      // do something with the result
    });
```

### In-app payments
1. Create ***KevinModule*** and register it in your ***ReactPackage*** class
2. Create new ***ReactMethod*** in ***KevinModule*** class that will be called from JS side and will open ***PaymentSessionActivity***:
```kotlin
class KevinModule (val context: ReactApplicationContext?) : ReactContextBaseJavaModule(context),
    ActivityEventListener {

    private var callback: Callback? = null

    init {
        context?.addActivityEventListener(this)
    }

    @ReactMethod
    fun makePayment(callback: Callback) {
        this.callback = callback
        val paymentSessionConfiguration = PaymentSessionConfiguration.Builder("state", PaymentType.BANK)
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
        val intent = Intent(currentActivity, PaymentSessionActivity::class.java)
        intent.putExtra(PaymentSessionContract.CONFIGURATION_KEY, paymentSessionConfiguration)

        context?.startActivityForResult(intent, REQUEST_CODE, null)
    }

    companion object {
        const val REQUEST_CODE = 1000
    }
}
```
3. Handle activity result inside ***KevinModule***:
```kotlin
class KevinModule (val context: ReactApplicationContext?) : ReactContextBaseJavaModule(context),
    ActivityEventListener {

    private var callback: Callback? = null
    
    override fun onActivityResult(
        activity: Activity?,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == REQUEST_CODE) {
            val result = data?.getParcelableExtra<ActivityResult<PaymentSessionResult>>(PaymentSessionContract.RESULT_KEY)
            when (result) {
                is ActivityResult.Success -> {
                    callback?.invoke(result.value.paymentId)
                }
                is ActivityResult.Canceled -> {
                    callback?.invoke("Canceled")
                }
                is ActivityResult.Failure -> {
                    callback?.invoke("Failed")
                }
            }
        }
    }

    companion object {
        const val REQUEST_CODE = 1000
    }
}
```
4. Call newly created ***ReactMethod*** from JS and handle received result:
```js
    import { NativeModules } from 'react-native';
    const { KevinModule } = NativeModules;
    
    KevinModule.makePayment(result => {
      // do something with the result
    });
```
## Examples

The ./demo folder contains a project showing how kevin. can be used.
