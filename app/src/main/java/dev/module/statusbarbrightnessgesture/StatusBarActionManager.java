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

    public static class ParsedIntent {
        public final String action;
        public final String pkg;
        public final String cls;

        public ParsedIntent(String action, String pkg, String cls) {
            this.action = action;
            this.pkg = pkg;
            this.cls = cls;
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

    public static ParsedIntent parseAction(String action) {
        if (action == null || !action.startsWith("intent:")) {
            return null;
        }

        String intentStr = action.substring(7);
        if (intentStr.contains("/")) {
            String[] parts = intentStr.split("/");
            return new ParsedIntent(null, parts[0], parts[1]);
        } else {
            return new ParsedIntent(intentStr, null, null);
        }
    }

    private boolean performAction(Context context, String action) {
        try {
            ParsedIntent pi = parseAction(action);
            if (pi == null) return false;

            Intent intent;
            if (pi.pkg != null && pi.cls != null) {
                intent = new Intent();
                intent.setClassName(pi.pkg, pi.cls);
            } else {
                intent = new Intent(pi.action);
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
