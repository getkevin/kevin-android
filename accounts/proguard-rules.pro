-keep,includedescriptorclasses class eu.kevin.accounts.**$$serializer { *; }
-keepclassmembers class eu.kevin.accounts.** {
    *** Companion;
}
-keepclasseswithmembers class eu.kevin.accounts.** {
    kotlinx.serialization.KSerializer serializer(...);
}