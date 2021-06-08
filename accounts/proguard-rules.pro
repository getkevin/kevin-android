# kotlinx.serialization

-keep,includedescriptorclasses class eu.kevin.accounts.networking.exceptions.**$$serializer { *; }
-keepclassmembers class eu.kevin.accounts.networking.exceptions.** {
    *** Companion;
}
-keepclasseswithmembers class eu.kevin.accounts.networking.exceptions.** {
    kotlinx.serialization.KSerializer serializer(...);
}