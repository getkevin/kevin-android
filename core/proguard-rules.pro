# kotlinx.serialization

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keep,includedescriptorclasses class eu.kevin.core.**$$serializer { *; }
-keepclassmembers class eu.kevin.core.** {
    *** Companion;
}
-keepclasseswithmembers class eu.kevin.core.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep public class io.ktor.client.** {
    public <methods>;
    private <methods>;
}

-dontwarn org.slf4j.impl.StaticLoggerBinder