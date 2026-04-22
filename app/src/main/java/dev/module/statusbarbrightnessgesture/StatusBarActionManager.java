package dev.module.statusbarbrightnessgesture;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.View;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XposedBridge;

public class StatusBarActionManager {
    private static final String TAG = "StatusBarActionManager";

    public enum Area {
        BATTERY,
        TIME,
        BACKGROUND
    }

    public static class ParsedAction {
        public final String intentAction;
        public final String pkg;
        public final String cls;
        public final String systemAction;

        public ParsedAction(String intentAction, String pkg, String cls, String systemAction) {
            this.intentAction = intentAction;
            this.pkg = pkg;
            this.cls = cls;
            this.systemAction = systemAction;
        }

        public boolean isSystem() {
            return systemAction != null;
        }

        public boolean isIntent() {
            return intentAction != null || (pkg != null && cls != null);
        }
    }

    private final Map<String, String> mActions = new HashMap<>();

    public void updateAction(Area area, StatusBarGestureDetector.GestureType gesture, String action) {
        mActions.put(getMapKey(area, gesture), action);
    }

    private String getMapKey(Area area, StatusBarGestureDetector.GestureType gesture) {
        return area.name() + "_" + gesture.name();
    }

    public boolean handleGesture(Context context, View view, StatusBarGestureDetector.GestureType gesture) {
        Area area = identifyArea(view);
        String action = mActions.get(getMapKey(area, gesture));
        
        if (action == null || action.isEmpty()) {
            return false;
        }

        return performAction(context, action);
    }

    private Area identifyArea(View view) {
        View current = view;
        while (current != null) {
            String name = current.getClass().getName();
            if (name.contains("BatteryMeterView")) {
                return Area.BATTERY;
            } else if (name.contains("Clock")) {
                return Area.TIME;
            }
            
            android.view.ViewParent parent = current.getParent();
            if (parent instanceof View) {
                current = (View) parent;
            } else {
                current = null;
            }
        }
        return Area.BACKGROUND;
    }

    public static ParsedAction parseAction(String action) {
        if (action == null) {
            return null;
        }

        if (action.startsWith("intent:")) {
            String intentStr = action.substring(7);
            if (intentStr.contains("/")) {
                String[] parts = intentStr.split("/");
                return new ParsedAction(null, parts[0], parts[1], null);
            } else {
                return new ParsedAction(intentStr, null, null, null);
            }
        } else if (action.startsWith("system:")) {
            return new ParsedAction(null, null, null, action.substring(7));
        }

        return null;
    }

    private boolean performAction(Context context, String action) {
        try {
            ParsedAction pa = parseAction(action);
            if (pa == null) return false;

            if (pa.isSystem()) {
                return performSystemAction(context, pa.systemAction);
            }

            Intent intent;
            if (pa.pkg != null && pa.cls != null) {
                intent = new Intent();
                intent.setClassName(pa.pkg, pa.cls);
            } else {
                intent = new Intent(pa.intentAction);
            }
            
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            XposedBridge.log(TAG + ": performed action: " + action);
            return true;
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": failed to perform action " + action + ": " + t);
            return false;
        }
    }

    boolean performSystemAction(Context context, String systemAction) {
        try {
            if ("toggle_dark_mode".equals(systemAction)) {
                UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
                if (uiModeManager == null) return false;
                int currentMode = uiModeManager.getNightMode();
                int newMode = (currentMode == UiModeManager.MODE_NIGHT_YES) ? UiModeManager.MODE_NIGHT_NO : UiModeManager.MODE_NIGHT_YES;
                uiModeManager.setNightMode(newMode);
                return true;
            } else if ("toggle_power_save".equals(systemAction)) {
                PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                if (powerManager == null) return false;
                boolean isEnabled = powerManager.isPowerSaveMode();
                try {
                    java.lang.reflect.Method setPowerSaveMode = powerManager.getClass().getMethod("setPowerSaveMode", boolean.class);
                    setPowerSaveMode.invoke(powerManager, !isEnabled);
                    return true;
                } catch (Throwable e) {
                    XposedBridge.log(TAG + ": reflection failed for setPowerSaveMode: " + e);
                    return false;
                }
            } else if ("lock_screen".equals(systemAction)) {
                PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                if (powerManager == null) return false;
                try {
                    java.lang.reflect.Method goToSleep = powerManager.getClass().getMethod("goToSleep", long.class);
                    goToSleep.invoke(powerManager, SystemClock.uptimeMillis());
                    return true;
                } catch (Throwable e) {
                    XposedBridge.log(TAG + ": reflection failed for goToSleep: " + e);
                    return false;
                }
            }
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": failed to perform system action " + systemAction + ": " + t);
        }
        return false;
    }
}
