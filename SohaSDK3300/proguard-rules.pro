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
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver

-keep public class com.s.sdk.base.BaseResponse{public <methods>;}
-keep public class com.s.sdk.network.RetrofitService{public <methods>;}
-keep public class com.s.sdk.LoginCallback{*;}
-keep public class com.s.sdk.LogoutCallback{*;}
-keep public class com.s.sdk.PaymentCallback{*;}
#model
-keep public class com.s.sdk.payment.model.**{*;}
-keep public class com.s.sdk.login.model.**{*;}
-keep public class com.s.sdk.init.model.**{*;}
-keep  class com.s.sdk.dashboard.model.DashBoardItem{ *;}

-keep public class com.s.sdk.SCallback{*;}
-keep public class com.s.sdk.utils.SPopup{public <methods>;}
-keep public class com.s.sdk.dashboard.view.DashBoardPopup{public <methods>;}
-keep public class com.s.sdk.SSDK{ public <methods>;}
-keep public class com.s.sdk.base.Constants{ *;}


-keep class org.eclipse.paho.** { *; }




#==============================================================

-keep class com.appsflyer.** {*;}
-dontwarn com.appsflyer.**
#===========================retrofit2===========================
-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepattributes EnclosingMethod

-keepclasseswithmembers class * {
    @retrofit2.* <methods>;
}

-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
#
## Platform calls Class.forName on types which do not exist on Android to determine platform.
#-dontnote retrofit2.Platform
## Platform used when running on RoboVM on iOS. Will not be used at runtime.
#-dontnote retrofit2.Platform$IOS$MainThreadExecutor
## Platform used when running on Java 8 VMs. Will not be used at runtime.
#-dontwarn retrofit2.Platform$Java8
## Retain generic type information for use by reflection by converters and adapters.
#-keepattributes Signature
## Retain declared checked exceptions for use by a Proxy instance.
#-keepattributes Exceptions


 #===========================Gson===========================
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
# -keep class mypersonalclass.data.model.** { *; }

  #===========================okhttp===========================
  # JSR 305 annotations are for embedding nullability information.
  -dontwarn javax.annotation.**

  # A resource is loaded with a relative path so the package of this class must be preserved.
  -keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

  # Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
  -dontwarn org.codehaus.mojo.animal_sniffer.*

  # OkHttp platform used only on JVM and when Conscrypt dependency is available.
  -dontwarn okhttp3.internal.platform.ConscryptPlatform


    #===========================google gcm===========================
  -keep class com.google.android.** { *; }
  -dontwarn com.google.android.**

#===========================Facebook===========================
-keep class com.facebook.** {
   *;
}
-keepattributes Signature
-keep class com.facebook.android.*
-keep class android.webkit.WebViewClient
-keep class * extends android.webkit.WebViewClient
-keepclassmembers class * extends android.webkit.WebViewClient {
    <methods>;
}