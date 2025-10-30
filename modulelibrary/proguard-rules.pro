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
# 避免混淆注解、内部类、泛型、匿名类
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod

#保持所有类的 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#表示不混淆 View 的子类中的 set 和 get 方法，因为 View 中的属性动画需要 setter 和 getter，混淆了就无法工作了
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();

}


#表示不混淆 Parcelable 的实现类中的 CREATOR，我们知道序列化与反序列化的过程都需要 CREATOR， 混淆了就无法工作了
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}



#保留四大组件，自定义的Application等这些类不被混淆
-keep public class * extends android.app.Activity

-keep public class * extends android.app.Service

-keep public class * extends android.content.BroadcastReceiver

-keep public class * extends android.content.ContentProvider

-keep public class * extends android.preference.Preference



#不混淆某个包所有的类
-keep class cn.net.aicare.algorithmutil.**{*;}
-keep class cn.net.aicare.**{*;}
-keep class com.besthealth.bhBodyComposition120.**{*;}
-keep class com.holtek.**{*;}
-keep class com.elinkthings.toothscore.**{*;}
-keep class cn.net.aicare.modulelibrary.module.**{*;}


# 不混淆指定包名下的类名，及类里的内容
-keep class com.elinkthings.httplibrary.**{*;}

