# kotlinx.serialization

-keep,includedescriptorclasses class eu.kevin.accounts.networking.exceptions.**$$serializer { *; }
-keepclassmembers class eu.kevin.accounts.networking.exceptions.** {
    *** Companion;
}
-keepclasseswithmembers class eu.kevin.accounts.networking.exceptions.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class eu.kevin.accounts.networking.entities.**$$serializer { *; }
-keepclassmembers class eu.kevin.accounts.networking.entities.** {
    *** Companion;
}
-keepclasseswithmembers class eu.kevin.accounts.networking.entities.** {
    kotlinx.serialization.KSerializer serializer(...);
}