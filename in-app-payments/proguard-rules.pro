-keep,includedescriptorclasses class eu.kevin.inapppayments.**$$serializer { *; }
-keepclassmembers class eu.kevin.inapppayments.** {
    *** Companion;
}
-keepclasseswithmembers class eu.kevin.inapppayments.** {
    kotlinx.serialization.KSerializer serializer(...);
}