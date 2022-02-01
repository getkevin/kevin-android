-keep public class io.ktor.client.** {
    public <methods>;
    private <methods>;
}

-dontwarn org.joda.money.**
-keep class org.joda.money.** { *; }
-keep interface org.joda.money.** { *; }