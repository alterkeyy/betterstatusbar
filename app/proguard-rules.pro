# ============================================================
# LSPosed module ProGuard rules
# ============================================================

# The hook class is referenced by name in assets/xposed_init.
# LSPosed finds it via Class.forName() at runtime, so its name
# must never be obfuscated or removed.
-keep class dev.module.betterstatusbar.BrightnessGestureHook { *; }

# Keep ModuleStatusChecker as it is hooked by the module and called by the app.
-keep class dev.module.betterstatusbar.ModuleStatusChecker { *; }

# Keep classes referenced in AndroidManifest.xml
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends androidx.fragment.app.Fragment

# Keep all LibXposed API classes (compileOnly, but some might be present or used in checks)
-keep class io.github.libxposed.api.** { *; }

# Resource adaptation
-adaptresourcefilenames META-INF/xposed/java_init.list
-adaptresourcefilenames META-INF/xposed/native_init.list

# BrightnessInfo is accessed via Display.getBrightnessInfo() — keep its fields.
-keep class android.hardware.display.BrightnessInfo { *; }

# Naming and Obfuscation
# Enabling obfuscation can significantly reduce size. 
# We keep the entry points above.

