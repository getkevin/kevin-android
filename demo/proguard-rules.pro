# kotlinx.serialization

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keep,includedescriptorclasses class eu.kevin.demo.**$$serializer { *; }
-keepclassmembers class eu.kevin.demo.** {
    *** Companion;
}
-keepclasseswithmembers class eu.kevin.demo.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep public class io.ktor.client.** {
    public <methods>;
    private <methods>;
}