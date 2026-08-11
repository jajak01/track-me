# Track Me ProGuard Rules
# Keep serializable models
-keep class com.trackme.app.data.model.** { *; }

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * { @retrofit2.http.* <methods>; }

# Kotlinx Serialization
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class kotlinx.serialization.** { *; }

# OkHttp
-keep class okhttp3.** { *; }

# MapLibre
-keep class org.maplibre.android.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
