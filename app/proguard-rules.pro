# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

-keep class com.example.** { *; }

-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

-keepattributes *Annotation*

# Descomenta esto para preservar la información del número de línea para
# trazas de stack de depuración.
#-keepattributes SourceFile,LineNumberTable

# Si mantienes la información del número de línea, descomenta esto para
# ocultar el nombre del archivo fuente original.
#-renamesourcefileattribute SourceFile
