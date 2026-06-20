# Kotlinx Serialization
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class io.github.claudio_santos.weathr.data.remote.dto.**$$serializer { *; }
-keepclassmembers class io.github.claudio_santos.weathr.data.remote.dto.** { *** Companion; }
-keepclasseswithmembers class io.github.claudio_santos.weathr.data.remote.dto.** { kotlinx.serialization.KSerializer serializer(...); }

# Ktor
-dontwarn org.slf4j.**
