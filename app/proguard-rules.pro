# ProGuard rules for SafeCall Recorder

# Keep Google API classes
-keep class com.google.api.** { *; }
-keep class com.google.auth.** { *; }

# Keep Room entities
-keep class com.safecall.recorder.data.local.db.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Kotlin serialization
-keepattributes *Annotation*
-keepattributes Signature

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
