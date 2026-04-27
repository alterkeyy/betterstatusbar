package dev.module.betterstatusbar;

public class ModuleStatusChecker {
    /**
     * This method is hooked by the Xposed module to return true.
     * If it returns false, the module is not active or not hooking correctly.
     */
    public static boolean isModuleActive() {
        return false;
    }

    /**
     * This method is hooked by the Xposed module to return the Xposed API version.
     * Returns -1 if the module is not active.
     */
    public static int getModuleApiVersion() {
        return -1;
    }

    /**
     * This method is hooked by the Xposed module to return the framework name (e.g. LSPosed, EdXposed).
     */
    public static String getModuleFramework() {
        return "Unknown";
    }

    /**
     * This method is hooked by the Xposed module to return the framework version string.
     */
    public static String getModuleFrameworkVersion() {
        return "Unknown";
    }
}