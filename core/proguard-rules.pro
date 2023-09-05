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

-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn java.lang.management.ManagementFactory
-dontwarn javax.management.InstanceNotFoundException
-dontwarn javax.management.MBeanRegistrationException
-dontwarn javax.management.MBeanServer
-dontwarn javax.management.MalformedObjectNameException
-dontwarn javax.management.ObjectInstance
-dontwarn javax.management.ObjectName
-dontwarn javax.naming.Context
-dontwarn javax.naming.InitialContext
-dontwarn javax.naming.NamingException
-dontwarn javax.servlet.ServletContainerInitializer
-dontwarn org.codehaus.janino.ClassBodyEvaluator
-dontwarn sun.reflect.Reflection

# kotlinx.serialization

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt