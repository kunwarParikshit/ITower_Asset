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
#-ignorewarnings


-keep class android.os.AsyncTask { *; }
-keep class com.google.gson** { *; }
-keepclassmembers class com.google.gson** {*;}
-dontwarn com.google.gson.Gson
-keepclassmembers class com.isl.modal** { <fields>; }
-keepclassmembers class com.isl.photo.camera** { <fields>; }
-keepclassmembers class com.isl.workflow.modal** { <fields>; }
-keep class org.mozilla.javascript** { *; }

-dontwarn org.apache.**
-keepclasseswithmembernames class *{
    native <methods>;
}

-keepclasseswithmembers class *{
    public <init>(android.content.Context,android.util.AttributeSet);
}
-dontwarn org.joda.convert.**
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn com.opencsv.bean.**
-dontwarn com.github.scribejava.**
-dontwarn com.googlecode.mp4parser.**
-keepclassmembers class * {
    private <fields>;
}

-keep class org.mozilla.geckoview.** { *; }
