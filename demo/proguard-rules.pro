# kotlinx.serialization

-keep,includedescriptorclasses class eu.kevin.sample.auth.entities.**$$serializer { *; }
-keepclassmembers class eu.kevin.demo.auth.entities.** {
    *** Companion;
}
-keepclasseswithmembers class eu.kevin.sample.auth.entities.** {
    kotlinx.serialization.KSerializer serializer(...);
}