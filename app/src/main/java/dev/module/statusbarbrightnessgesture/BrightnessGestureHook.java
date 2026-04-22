package dev.module.statusbarbrightnessgesture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.Executors;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * LSPosed module — status bar brightness gesture.
 */
@SuppressWarnings({"JavaReflectionMemberAccess", "ConstantConditions"})
public class BrightnessGestureHook implements IXposedHookLoadPackage {

    private static final String TAG = "BrightnessGestureHook";
    private static final String SYSTEMUI_PACKAGE = "com.android.systemui";

    private static final String PHONE_STATUS_BAR_VIEW =
            "com.android.systemui.statusbar.phone.PhoneStatusBarView";
    private static final String SHADE_WINDOW_CLASS =
            "com.android.systemui.shade.NotificationShadeWindowView";
    private static final String BRIGHTNESS_UTILS_CLASS =
            "com.android.settingslib.display.BrightnessUtils";

    private static final int GAMMA_SPACE_MAX = 65535;
    private static final float STATUS_BAR_Y_FRACTION = 0.06f;
    private static final float HORIZONTAL_RATIO = 2.0f;
    private static final float GAMMA = 2.2f;
    private static final long INDICATOR_DISMISS_DELAY_MS = 800;

    // ── Per-gesture state ─────────────────────────────────────────────────────

    private float mDownX;
    private float mDownY;
    private float mInitialBrightness;
    private boolean mGestureActive = false;
    private boolean mTouchStartedInStatusBar = false;

    // ── Cached resources ──────────────────────────────────────────────────────

    private DisplayManager mDisplayManager;
    private WindowManager mWindowManager;
    private int mScreenWidth;
    private int mScreenHeight;
    private float mGestureSlopPx = 48f;
    private float mBrightnessMin = -1f;
    private float mBrightnessMax = 1.0f;

    private Method mSetTemporaryBrightnessMethod;
    private Method mSetBrightnessMethod;
    private Method mGetBrightnessInfoMethod;
    private Method mConvertLinearToGammaMethod;

    private Field mBrightnessField;
    private Field mBrightnessMinField;
    private Field mBrightnessMaxField;

    private final java.util.concurrent.ExecutorService mBgExecutor =
            Executors.newSingleThreadExecutor();
    private Handler mMainHandler;

    // ── Indicator ─────────────────────────────────────────────────────────────

    private TextView mIndicatorView;
    private WindowManager.LayoutParams mIndicatorParams;
    private boolean mIndicatorAttached = false;
    private final Runnable mDismissIndicator = this::hideIndicator;

    // ── Action Management ─────────────────────────────────────────────────────

    private final StatusBarActionManager mActionManager = new StatusBarActionManager();
    private StatusBarGestureDetector mGestureDetector;

    // ── Prefs ─────────────────────────────────────────────────────────────────

    private boolean mReceiverRegistered = false;
    private volatile boolean mGestureEnabled = true;
    private volatile boolean mOverlayEnabled  = true;
    private volatile boolean mRelativeEnabled = false;

    // ── Entry point ───────────────────────────────────────────────────────────

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!SYSTEMUI_PACKAGE.equals(lpparam.packageName)) return;

        XposedBridge.log(TAG + ": loading in SystemUI");

        Method hookMethodFn = findHookMethod();
        if (hookMethodFn == null) {
            XposedBridge.log(TAG + ": could not find hookMethod() — aborting");
            return;
        }

        hookTouchTarget(PHONE_STATUS_BAR_VIEW, "onTouchEvent",
                lpparam.classLoader, hookMethodFn, true);
        hookTouchTarget(SHADE_WINDOW_CLASS, "dispatchTouchEvent",
                lpparam.classLoader, hookMethodFn, false);
        hookAttachedToWindow(PHONE_STATUS_BAR_VIEW,
                lpparam.classLoader, hookMethodFn);
    }

    private Method findHookMethod() {
        for (Method m : XposedBridge.class.getDeclaredMethods()) {
            Class<?>[] params = m.getParameterTypes();
            if (params.length == 2
                    && java.lang.reflect.Member.class.isAssignableFrom(params[0])
                    && XC_MethodHook.class.isAssignableFrom(params[1])) {
                m.setAccessible(true);
                return m;
            }
        }
        return null;
    }

    private void hookAttachedToWindow(String className, ClassLoader classLoader,
                                      Method hookMethodFn) {
        try {
            Class<?> cls = Class.forName(className, false, classLoader);
            Method target = cls.getDeclaredMethod("onAttachedToWindow");
            hookMethodFn.invoke(null, target, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    try {
                        Context ctx = (Context) param.thisObject.getClass()
                                .getMethod("getContext").invoke(param.thisObject);
                        if (ctx == null) return;
                        if (!mReceiverRegistered) registerPrefsReceiver(ctx);
                        if (mDisplayManager == null) initDisplayResources(ctx);
                    } catch (Throwable t) {
                        XposedBridge.log(TAG + ": onAttachedToWindow init failed: " + t);
                    }
                }
            });
            XposedBridge.log(TAG + ": hooked " + className + ".onAttachedToWindow");
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": failed to hook onAttachedToWindow: " + t);
        }
    }

    private void registerPrefsReceiver(Context context) {
        if (mReceiverRegistered) return;
        mReceiverRegistered = true;

        updatePrefsFromContext(context);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (!Prefs.ACTION_PREFS_CHANGED.equals(intent.getAction())) return;
                boolean prevGesture = mGestureEnabled;
                
                mGestureEnabled = intent.getBooleanExtra(Prefs.KEY_GESTURE_ENABLED, true);
                mOverlayEnabled  = intent.getBooleanExtra(Prefs.KEY_OVERLAY_ENABLED,  true);
                mRelativeEnabled = intent.getBooleanExtra(Prefs.KEY_RELATIVE_BRIGHTNESS, false);

                // Update Actions
                updateActionsFromIntent(intent);

                XposedBridge.log(TAG + ": prefs updated via broadcast");
                if (prevGesture && !mGestureEnabled && mIndicatorAttached) {
                    hideIndicator();
                }
            }
        };

        IntentFilter filter = new IntentFilter(Prefs.ACTION_PREFS_CHANGED);
        context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
        XposedBridge.log(TAG + ": prefs receiver registered");
    }

    private void updatePrefsFromContext(Context context) {
        try {
            mGestureEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    Prefs.KEY_GESTURE_ENABLED, Prefs.DEFAULT_GESTURE_ENABLED) == 1;
            mOverlayEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    Prefs.KEY_OVERLAY_ENABLED, Prefs.DEFAULT_OVERLAY_ENABLED) == 1;
            mRelativeEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    Prefs.KEY_RELATIVE_BRIGHTNESS, Prefs.DEFAULT_RELATIVE_BRIGHTNESS) == 1;

            updateActionsFromSecureSettings(context);
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": updatePrefsFromContext failed: " + t);
        }
    }

    private void updateActionsFromIntent(Intent intent) {
        // Battery
        mActionManager.updateAction(StatusBarActionManager.Area.BATTERY, StatusBarGestureDetector.GestureType.SINGLE_TAP, intent.getStringExtra(Prefs.KEY_BATTERY_SINGLE_TAP_ACTION));
        mActionManager.updateAction(StatusBarActionManager.Area.BATTERY, StatusBarGestureDetector.GestureType.DOUBLE_TAP, intent.getStringExtra(Prefs.KEY_BATTERY_DOUBLE_TAP_ACTION));
        mActionManager.updateAction(StatusBarActionManager.Area.BATTERY, StatusBarGestureDetector.GestureType.LONG_TAP,   intent.getStringExtra(Prefs.KEY_BATTERY_LONG_TAP_ACTION));

        // Time
        mActionManager.updateAction(StatusBarActionManager.Area.TIME, StatusBarGestureDetector.GestureType.SINGLE_TAP, intent.getStringExtra(Prefs.KEY_TIME_SINGLE_TAP_ACTION));
        mActionManager.updateAction(StatusBarActionManager.Area.TIME, StatusBarGestureDetector.GestureType.DOUBLE_TAP, intent.getStringExtra(Prefs.KEY_TIME_DOUBLE_TAP_ACTION));
        mActionManager.updateAction(StatusBarActionManager.Area.TIME, StatusBarGestureDetector.GestureType.LONG_TAP,   intent.getStringExtra(Prefs.KEY_TIME_LONG_TAP_ACTION));

        // Background
        mActionManager.updateAction(StatusBarActionManager.Area.BACKGROUND, StatusBarGestureDetector.GestureType.SINGLE_TAP, intent.getStringExtra(Prefs.KEY_STATUSBAR_SINGLE_TAP_ACTION));
        mActionManager.updateAction(StatusBarActionManager.Area.BACKGROUND, StatusBarGestureDetector.GestureType.DOUBLE_TAP, intent.getStringExtra(Prefs.KEY_STATUSBAR_DOUBLE_TAP_ACTION));
        mActionManager.updateAction(StatusBarActionManager.Area.BACKGROUND, StatusBarGestureDetector.GestureType.LONG_TAP,   intent.getStringExtra(Prefs.KEY_STATUSBAR_LONG_TAP_ACTION));
    }

    private void updateActionsFromSecureSettings(Context context) {
        // Battery
        mActionManager.updateAction(StatusBarActionManager.Area.BATTERY, StatusBarGestureDetector.GestureType.SINGLE_TAP, getStringSafe(context, Prefs.KEY_BATTERY_SINGLE_TAP_ACTION, Prefs.DEFAULT_ACTION_BATTERY_TAP));
        mActionManager.updateAction(StatusBarActionManager.Area.BATTERY, StatusBarGestureDetector.GestureType.DOUBLE_TAP, getStringSafe(context, Prefs.KEY_BATTERY_DOUBLE_TAP_ACTION, ""));
        mActionManager.updateAction(StatusBarActionManager.Area.BATTERY, StatusBarGestureDetector.GestureType.LONG_TAP,   getStringSafe(context, Prefs.KEY_BATTERY_LONG_TAP_ACTION, ""));

        // Time
        mActionManager.updateAction(StatusBarActionManager.Area.TIME, StatusBarGestureDetector.GestureType.SINGLE_TAP, getStringSafe(context, Prefs.KEY_TIME_SINGLE_TAP_ACTION, Prefs.DEFAULT_ACTION_TIME_TAP));
        mActionManager.updateAction(StatusBarActionManager.Area.TIME, StatusBarGestureDetector.GestureType.DOUBLE_TAP, getStringSafe(context, Prefs.KEY_TIME_DOUBLE_TAP_ACTION, ""));
        mActionManager.updateAction(StatusBarActionManager.Area.TIME, StatusBarGestureDetector.GestureType.LONG_TAP,   getStringSafe(context, Prefs.KEY_TIME_LONG_TAP_ACTION, ""));

        // Background
        mActionManager.updateAction(StatusBarActionManager.Area.BACKGROUND, StatusBarGestureDetector.GestureType.SINGLE_TAP, getStringSafe(context, Prefs.KEY_STATUSBAR_SINGLE_TAP_ACTION, ""));
        mActionManager.updateAction(StatusBarActionManager.Area.BACKGROUND, StatusBarGestureDetector.GestureType.DOUBLE_TAP, getStringSafe(context, Prefs.KEY_STATUSBAR_DOUBLE_TAP_ACTION, ""));
        mActionManager.updateAction(StatusBarActionManager.Area.BACKGROUND, StatusBarGestureDetector.GestureType.LONG_TAP,   getStringSafe(context, Prefs.KEY_STATUSBAR_LONG_TAP_ACTION, ""));
    }

    private String getStringSafe(Context context, String key, String def) {
        String val = Settings.Secure.getString(context.getContentResolver(), key);
        return val != null ? val : def;
    }

    private void hookTouchTarget(String className, String methodName,
                                 ClassLoader classLoader, Method hookMethodFn,
                                 final boolean isStatusBarView) {
        try {
            Class<?> cls = Class.forName(className, false, classLoader);
            Method target = cls.getDeclaredMethod(methodName, MotionEvent.class);
            hookMethodFn.invoke(null, target, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    MotionEvent ev = (MotionEvent) param.args[0];
                    if (ev == null) return;
                    if (mDisplayManager == null) {
                        try {
                            Context ctx = (Context) param.thisObject.getClass()
                                    .getMethod("getContext").invoke(param.thisObject);
                            if (ctx != null) initDisplayResources(ctx);
                        } catch (Throwable t) {
                            XposedBridge.log(TAG + ": display init failed: " + t);
                        }
                    }

                    if (handleTouchEvent(ev, isStatusBarView, (View) param.thisObject)) {
                        param.setResult(true);
                    }
                }
            });
            XposedBridge.log(TAG + ": hooked " + className + "." + methodName);
        } catch (ClassNotFoundException e) {
            XposedBridge.log(TAG + ": class not found: " + className);
        } catch (NoSuchMethodException e) {
            XposedBridge.log(TAG + ": method not found: " + methodName);
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": hook failed for " + className + ": " + t);
        }
    }

    private void initDisplayResources(Context context) {
        try {
            if (mMainHandler == null) mMainHandler = new Handler(Looper.getMainLooper());
            mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            mWindowManager  = (WindowManager)  context.getSystemService(Context.WINDOW_SERVICE);

            android.graphics.Rect bounds = mWindowManager.getCurrentWindowMetrics().getBounds();
            mScreenWidth  = bounds.width();
            mScreenHeight = bounds.height();

            float density = context.getResources().getDisplayMetrics().density;
            mGestureSlopPx = Math.max(
                    ViewConfiguration.get(context).getScaledTouchSlop(), 12f * density);

            mSetTemporaryBrightnessMethod = DisplayManager.class
                    .getDeclaredMethod("setTemporaryBrightness", int.class, float.class);
            mSetTemporaryBrightnessMethod.setAccessible(true);

            mSetBrightnessMethod = DisplayManager.class
                    .getDeclaredMethod("setBrightness", int.class, float.class);
            mSetBrightnessMethod.setAccessible(true);

            mGetBrightnessInfoMethod = Display.class.getDeclaredMethod("getBrightnessInfo");
            mGetBrightnessInfoMethod.setAccessible(true);

            try {
                Class<?> bu = Class.forName(
                        BRIGHTNESS_UTILS_CLASS, false, context.getClassLoader());
                mConvertLinearToGammaMethod = bu.getMethod(
                        "convertLinearToGammaFloat", float.class, float.class, float.class);
                XposedBridge.log(TAG + ": found BrightnessUtils.convertLinearToGammaFloat");
            } catch (Throwable t) {
                XposedBridge.log(TAG + ": BrightnessUtils not found, using fallback");
            }

            if (mGestureDetector == null) {
                mGestureDetector = new StatusBarGestureDetector(context, (view, ev, type) -> {
                    mActionManager.handleGesture(context, findTappedView(view, ev.getX(), ev.getY()), type);
                });
            }

            readBrightnessRange();
            initIndicator(context);

            XposedBridge.log(TAG + ": display init complete");
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": initDisplayResources failed: " + t);
        }
    }

    private View findTappedView(View root, float x, float y) {
        if (!(root instanceof android.view.ViewGroup)) {
            return root;
        }
        android.view.ViewGroup group = (android.view.ViewGroup) root;
        for (int i = group.getChildCount() - 1; i >= 0; i--) {
            View child = group.getChildAt(i);
            if (child.getVisibility() != View.VISIBLE) continue;
            float childX = x - child.getLeft();
            float childY = y - child.getTop();
            if (childX >= 0 && childX < child.getWidth() && childY >= 0 && childY < child.getHeight()) {
                return findTappedView(child, childX, childY);
            }
        }
        return root;
    }

    private void readBrightnessRange() {
        try {
            Display display = mDisplayManager.getDisplay(Display.DEFAULT_DISPLAY);
            if (display == null) return;
            Object info = mGetBrightnessInfoMethod.invoke(display);
            if (info == null) return;
            Class<?> cls = info.getClass();
            if (mBrightnessMinField == null) {
                mBrightnessField    = cls.getField("brightness");
                mBrightnessMinField = cls.getField("brightnessMinimum");
                mBrightnessMaxField = cls.getField("brightnessMaximum");
            }
            mBrightnessMin = (float) mBrightnessMinField.get(info);
            mBrightnessMax = (float) mBrightnessMaxField.get(info);
        } catch (Throwable t) {
            mBrightnessMin = 0.0f;
            mBrightnessMax = 1.0f;
            XposedBridge.log(TAG + ": readBrightnessRange fallback: " + t);
        }
    }

    private void initIndicator(Context context) {
        try {
            int accent  = resolveAccentColour(context);
            int textCol = getContrastingTextColour(accent);

            mIndicatorView = new TextView(context);
            mIndicatorView.setTextColor(textCol);
            mIndicatorView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 13f);
            mIndicatorView.setTypeface(Typeface.DEFAULT_BOLD);
            mIndicatorView.setGravity(Gravity.CENTER);

            android.graphics.drawable.GradientDrawable bg =
                    new android.graphics.drawable.GradientDrawable();
            bg.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
            bg.setCornerRadius(100f);
            bg.setColor(accent);
            mIndicatorView.setBackground(bg);

            float d = context.getResources().getDisplayMetrics().density;
            mIndicatorView.setPadding(
                    (int)(14*d), (int)(6*d), (int)(14*d), (int)(6*d));

            mIndicatorParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT);
            mIndicatorParams.gravity = Gravity.TOP | Gravity.START;
            mIndicatorView.setAlpha(0f);
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": initIndicator failed: " + t);
        }
    }

    private int resolveAccentColour(Context context) {
        try { return context.getColor(android.R.color.system_accent1_600); }
        catch (Throwable t) { return Color.argb(210, 30, 30, 30); }
    }

    private int getContrastingTextColour(int bg) {
        double r = Color.red(bg)/255.0, g = Color.green(bg)/255.0, b = Color.blue(bg)/255.0;
        r = r<=0.03928?r/12.92:Math.pow((r+0.055)/1.055, 2.4);
        g = g<=0.03928?g/12.92:Math.pow((g+0.055)/1.055, 2.4);
        b = b<=0.03928?b/12.92:Math.pow((b+0.055)/1.055, 2.4);
        return (0.2126*r + 0.7152*g + 0.0722*b) < 0.35 ? Color.WHITE : Color.BLACK;
    }

    private void showIndicator(float fingerX, float linearBrightness) {
        if (mIndicatorView == null || mWindowManager == null || mMainHandler == null) return;
        if (!mOverlayEnabled) return;

        mMainHandler.removeCallbacks(mDismissIndicator);

        int pct = linearToDisplayPct(linearBrightness);
        mIndicatorView.setText(pct + "%");
        mIndicatorView.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(0,
                        android.view.View.MeasureSpec.UNSPECIFIED),
                android.view.View.MeasureSpec.makeMeasureSpec(0,
                        android.view.View.MeasureSpec.UNSPECIFIED));

        int viewW   = mIndicatorView.getMeasuredWidth();
        int yOffset = (int)(mScreenHeight * 0.055f);
        int xOffset = Math.max(4, Math.min(mScreenWidth - viewW - 4,
                (int)(fingerX - viewW / 2f)));

        mIndicatorParams.x = xOffset;
        mIndicatorParams.y = yOffset;

        try {
            if (!mIndicatorAttached) {
                mWindowManager.addView(mIndicatorView, mIndicatorParams);
                mIndicatorAttached = true;
            } else {
                mWindowManager.updateViewLayout(mIndicatorView, mIndicatorParams);
            }
            mIndicatorView.setAlpha(1f);
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": showIndicator failed: " + t);
        }
    }

    private int linearToDisplayPct(float linear) {
        try {
            if (mConvertLinearToGammaMethod != null) {
                int gammaVal = (int) mConvertLinearToGammaMethod.invoke(
                        null, linear, mBrightnessMin, mBrightnessMax);
                return Math.max(0, Math.min(100,
                        Math.round((float) gammaVal / GAMMA_SPACE_MAX * 100f)));
            }
        } catch (Throwable ignored) {}
        float range = mBrightnessMax - mBrightnessMin;
        if (range <= 0) return 0;
        float n = Math.max(0f, Math.min(1f, (linear - mBrightnessMin) / range));
        return Math.max(0, Math.min(100,
                Math.round((float) Math.pow(n, 1.0 / GAMMA) * 100f)));
    }

    private void hideIndicator() {
        if (mIndicatorView == null || mMainHandler == null) return;
        mMainHandler.removeCallbacks(mDismissIndicator);
        mIndicatorView.animate().alpha(0f).setDuration(200).withEndAction(() -> {
            if (mIndicatorAttached && mWindowManager != null) {
                try { mWindowManager.removeView(mIndicatorView); }
                catch (Throwable ignored) {}
                mIndicatorAttached = false;
            }
        }).start();
    }

    private boolean handleTouchEvent(MotionEvent ev, boolean isStatusBarView, View view) {
        if (mDisplayManager == null || mScreenWidth == 0) return false;

        if (mGestureDetector != null) {
            mGestureDetector.onTouchEvent(view, ev);
        }

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:   return onDown(ev, isStatusBarView);
            case MotionEvent.ACTION_MOVE:   return onMove(ev);
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: return onUpOrCancel(ev);
            default: return false;
        }
    }

    private boolean onDown(MotionEvent ev, boolean isStatusBarView) {
        mGestureActive = false;
        mTouchStartedInStatusBar = false;
        boolean inRegion = isStatusBarView
                || (ev.getY() <= mScreenHeight * STATUS_BAR_Y_FRACTION);
        if (!inRegion) return false;
        mTouchStartedInStatusBar = true;
        mDownX = ev.getX();
        mDownY = ev.getY();
        mInitialBrightness = getCurrentBrightness();
        return false;
    }

    private boolean onMove(MotionEvent ev) {
        if (!mTouchStartedInStatusBar) return false;
        if (!mGestureEnabled) return false;

        float absDX = Math.abs(ev.getX() - mDownX);
        float absDY = Math.abs(ev.getY() - mDownY);
        if (!mGestureActive) {
            if (absDX <= mGestureSlopPx || absDX <= absDY * HORIZONTAL_RATIO) return false;
            mGestureActive = true;
        }
        float brightness = computeBrightness(ev.getX());
        setTemporaryBrightness(brightness);
        showIndicator(ev.getX(), brightness);
        return true;
    }

    private boolean onUpOrCancel(MotionEvent ev) {
        if (!mGestureActive) {
            mTouchStartedInStatusBar = false;
            return false;
        }
        boolean cancelled = ev.getActionMasked() == MotionEvent.ACTION_CANCEL;
        float finalBrightness = cancelled
                ? getCurrentBrightness()
                : computeBrightness(ev.getX());
        setTemporaryBrightness(finalBrightness);
        commitBrightness(finalBrightness);
        if (mMainHandler != null)
            mMainHandler.postDelayed(mDismissIndicator, INDICATOR_DISMISS_DELAY_MS);
        mGestureActive = false;
        mTouchStartedInStatusBar = false;
        return true;
    }

    private float computeBrightness(float fingerX) {
        if (mBrightnessMin < 0) readBrightnessRange();
        if (mRelativeEnabled) {
            return BrightnessCalculator.computeRelativeBrightness(
                    mInitialBrightness, mDownX, fingerX, mScreenWidth, mBrightnessMin, mBrightnessMax);
        } else {
            return BrightnessCalculator.computeAbsoluteBrightness(
                    fingerX, mScreenWidth, mBrightnessMin, mBrightnessMax);
        }
    }

    private void setTemporaryBrightness(float brightness) {
        if (mSetTemporaryBrightnessMethod == null) return;
        try { mSetTemporaryBrightnessMethod.invoke(
                mDisplayManager, Display.DEFAULT_DISPLAY, brightness); }
        catch (Throwable t) { XposedBridge.log(TAG + ": setTemporaryBrightness: " + t); }
    }

    private void commitBrightness(float brightness) {
        if (mSetBrightnessMethod == null) return;
        mBgExecutor.execute(() -> {
            try { mSetBrightnessMethod.invoke(
                    mDisplayManager, Display.DEFAULT_DISPLAY, brightness); }
            catch (Throwable t) { XposedBridge.log(TAG + ": setBrightness: " + t); }
        });
    }

    private float getCurrentBrightness() {
        try {
            if (mGetBrightnessInfoMethod == null || mBrightnessField == null) return 0.5f;
            Display display = mDisplayManager.getDisplay(Display.DEFAULT_DISPLAY);
            if (display == null) return 0.5f;
            Object info = mGetBrightnessInfoMethod.invoke(display);
            if (info == null) return 0.5f;
            float b = (float) mBrightnessField.get(info);
            return Math.max(mBrightnessMin, Math.min(mBrightnessMax, b));
        } catch (Throwable t) { return 0.5f; }
    }
}
