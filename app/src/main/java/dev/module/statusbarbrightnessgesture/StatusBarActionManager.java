package dev.module.statusbarbrightnessgesture;

import android.content.Context;
import android.content.Intent;
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
                // To be implemented in next task
                XposedBridge.log(TAG + ": system action not yet implemented: " + pa.systemAction);
                return false;
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
}
