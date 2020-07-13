# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keepclassmembers class com.ihpukan.nks.common.JavaScriptTokenIntercept {
   public *;
}
#-keepclassmembers class * {
#     @android.webkit.JavascriptInterface <methods>;
# }

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule
-keepclassmembers class android.support.v4.app.** { *; }
-keepclassmembers interface android.support.v4.app.** { *; }
-keepclassmembers class android.support.v7.app.** { *; }
-keepclassmembers interface android.support.v7.app.** { *; }
-keepclassmembers class android.support.v7.widget.SearchView { *; }

# support-v7-appcompat
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
# support-design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

#-keep class com.squareup.picasso.**

#-keep class com.squareup.retrofit2.**
 #### -- OkHttp --

#-keep class com.jakewharton.picasso.**

#-keep class com.squareup.okhttp3.internal.**

#-keep class com.squareup.okhttp3.**

-dontwarn com.squareup.okhttp.**

 #### -- Apache Commons --

#-keep class org.apache.commons.logging.**

-dontwarn java.lang.invoke.**
-dontwarn java.nio.file.**

-dontwarn org.codehaus.mojo.animal_sniffer.**



-dontwarn com.google.errorprone.annotations.**

#-keep class retrofit2.** {*;}
#-keep class okio.** {*;}
#-keep class com.squareup.picasso.** {*;}
#-keep class dagger.android.** {*;}

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

-keep class com.bumptech.** {*;}
-keepclassmembers class com.bumptech.** {*;}
