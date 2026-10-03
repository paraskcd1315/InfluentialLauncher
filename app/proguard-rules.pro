-keepclassmembers class rikka.shizuku.Shizuku {
    *** newProcess(...);
}

# The shell helper is loaded by name inside app_process; keep it and its binder transport.
-keep class com.paraskcd.influentiallauncher.shellaccess.infrastructure.server.InfShellServer { *; }
-keep class com.paraskcd.influentiallauncher.shellaccess.IInfShell { *; }
-keep class com.paraskcd.influentiallauncher.shellaccess.IInfShell$Stub { *; }
-keep class com.paraskcd.influentiallauncher.shellaccess.infrastructure.transport.BinderContainer {
    public static final ** CREATOR;
    *;
}
# libadb and its BouncyCastle / Conscrypt crypto.
-keep class io.github.muntashirakon.adb.** { *; }
-keep class org.conscrypt.** { *; }
-dontwarn org.conscrypt.**
-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**
