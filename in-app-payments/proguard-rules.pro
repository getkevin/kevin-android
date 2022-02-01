# kotlinx.serialization

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keep,includedescriptorclasses class eu.kevin.inapppayments.**$$serializer { *; }
-keepclassmembers class eu.kevin.inapppayments.** {
    *** Companion;
}
-keepclasseswithmembers class eu.kevin.inapppayments.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep public class io.ktor.client.** {
    public <methods>;
    private <methods>;
}