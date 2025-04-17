# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# ======= GENERAL KEEP RULES =======

# Keep all your app classes and methods
-keep class com.satnamsinghmaggo.nitnemgurbani.** { *; }

# Keep Activities, Fragments, and custom Views
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends androidx.fragment.app.Fragment
-keep class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep layout callbacks (e.g., onClick)
-keepclassmembers class * {
    public void *(android.view.View);
}

# ======= LOTTIE (Animations) =======
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

# ======= PDFView (Barteksc PDF Viewer) =======
-keep class com.github.barteksc.** { *; }
-dontwarn com.github.barteksc.**

# ======= OKHTTP (Network) =======
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# ======= JSoup (HTML Parser) =======
-keep class org.jsoup.** { *; }
-dontwarn org.jsoup.**

# ======= GSON (Optional JSON Parsing) =======
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# ======= Android MediaPlayer =======
-dontwarn android.media.**

# ======= Prevent stripping of all annotations (safe) =======
-keepattributes *Annotation*

# ======= Preserve all enums (used in libraries) =======
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ======= Avoid warnings about missing debug info =======
-dontwarn org.codehaus.**

# ======= Remove logging for release builds (optional) =======
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
