# kotlinx.serialization

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keep,includedescriptorclasses class eu.kevin.accounts.**$$serializer { *; }
-keepclassmembers class eu.kevin.accounts.** {
    *** Companion;
}
-keepclasseswithmembers class eu.kevin.accounts.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep public class io.ktor.client.** {
    public <methods>;
    private <methods>;
}

-dontwarn org.slf4j.impl.StaticLoggerBinder