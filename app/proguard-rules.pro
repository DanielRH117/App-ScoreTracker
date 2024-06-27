# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Mantener todas las clases del paquete com.example sin ofuscar
-keep class com.example.** { *; }

# Mantener todos los métodos de las clases derivadas de android.app.Activity
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# Mantener todas las anotaciones
-keepattributes *Annotation*

# Si tu proyecto utiliza WebView con JavaScript, descomenta lo siguiente
# y especifica el nombre de clase completamente calificado para la interfaz JavaScript:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Descomenta esto para preservar la información del número de línea para
# trazas de stack de depuración.
#-keepattributes SourceFile,LineNumberTable

# Si mantienes la información del número de línea, descomenta esto para
# ocultar el nombre del archivo fuente original.
#-renamesourcefileattribute SourceFile
