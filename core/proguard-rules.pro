# kotlinx.serialization

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class eu.kevin.core.networking.exceptions.**$$serializer { *; }
-keep,includedescriptorclasses class eu.kevin.core.networking.entities.**$$serializer { *; }
-keepclassmembers class eu.kevin.core.networking.exceptions.** {
    *** Companion;
}
-keepclassmembers class eu.kevin.core.networking.entities.** {
    *** Companion;
}
-keepclasseswithmembers class eu.kevin.core.networking.exceptions.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclasseswithmembers class eu.kevin.core.networking.entities.** {
    kotlinx.serialization.KSerializer serializer(...);
}