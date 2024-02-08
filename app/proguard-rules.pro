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
-dontobfuscate
-keep class com.mrikso.kodikdownloader.** { *; }
-keep,allowobfuscation,allowshrinking interface com.mrikso.kodikdownloader.service.KodikService

## GSON 2.2.4 specific rules ##

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

-keepattributes EnclosingMethod

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

 # Keep generic signature of RxJava3 (R8 full mode strips signatures from non-kept items). 
-keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Flowable 
-keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Maybe 
 -keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Observable 
 -keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Single 
 # Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items). 
 -keep,allowobfuscation,allowshrinking interface retrofit2.Call 
 -keep,allowobfuscation,allowshrinking class retrofit2.Response 
  
 # With R8 full mode generic signatures are stripped for classes that are not 
 # kept. Suspend functions are wrapped in continuations where the type argument 
 # is used. 
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation 
