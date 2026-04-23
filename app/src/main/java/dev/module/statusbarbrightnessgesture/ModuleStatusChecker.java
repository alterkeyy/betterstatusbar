package dev.module.statusbarbrightnessgesture;

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
}