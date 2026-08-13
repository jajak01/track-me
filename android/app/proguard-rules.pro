# Keep kotlinx.serialization serializers
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class com.desen.trackme.** {
    *** Companion;
}
-keepclasseswithmembers class com.desen.trackme.** {
    kotlinx.serialization.KSerializer serializer(...);
}
