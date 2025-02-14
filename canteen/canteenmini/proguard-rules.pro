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
-keep class com.stkj.cashiermini.model.**{*;}
-keep class com.stkj.cashiermini.base.model.**{*;}
-keep class com.stkj.cashiermini.home.model.**{*;}
-keep class com.stkj.cashiermini.login.model.**{*;}
-keep class com.stkj.cashiermini.pay.model.**{*;}
-keep class com.stkj.cashiermini.stat.model.**{*;}
-keep class com.stkj.cashiermini.setting.model.**{*;}
-keep class * extends com.stkj.common.core.ActivityWeakRefHolder{*;}