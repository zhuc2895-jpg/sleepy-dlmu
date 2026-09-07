# Amadeus Kurisu - keep JS bridge interface
-keep class com.amadeus.kurisu.MainActivity$AmadeusBridge { *; }
-keepclassmembers class com.amadeus.kurisu.MainActivity$AmadeusBridge {
    @android.webkit.JavascriptInterface <methods>;
}
