-keep,includedescriptorclasses class eu.kevin.common.**$$serializer { *; }
-keepclassmembers class eu.kevin.common.** {
    *** Companion;
}
-keepclasseswithmembers class eu.kevin.common.** {
    kotlinx.serialization.KSerializer serializer(...);
}