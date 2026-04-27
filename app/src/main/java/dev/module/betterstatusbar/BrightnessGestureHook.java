package dev.module.betterstatusbar;

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
import android.util.Log;
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

import androidx.annotation.NonNull;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface;

/**
 * LSPosed module — status bar brightness gesture.
 */
@SuppressWarnings({"JavaReflectionMemberAccess", "ConstantConditions"})
public class BrightnessGestureHook extends XposedModule {

    private static final String TAG = "BrightnessGestureHook";
    private static final String SYSTEMUI_PACKAGE = "com.android.systemui";

    public BrightnessGestureHook() {
        super();
    }

    private void logMsg(String msg) {
        log(Log.DEBUG, TAG, msg);
    }

    private void logErr(String msg, Throwable t) {
        log(Log.ERROR, TAG, msg, t);
    }

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
    private final HapticHandler mHapticHandler = new HapticHandler();

    // ── Prefs ─────────────────────────────────────────────────────────────────

    private boolean mReceiverRegistered = false;
    private volatile boolean mGestureEnabled = true;
    private volatile boolean mOverlayEnabled  = true;
    private volatile boolean mRelativeEnabled = false;
    private volatile boolean mLoggingEnabled  = false;
    private volatile int mHapticIntensity = Prefs.DEFAULT_HAPTIC_INTENSITY;
    private volatile int mSwipeSensitivity = Prefs.DEFAULT_SWIPE_SENSITIVITY;

    // ── Entry point ───────────────────────────────────────────────────────────

    @Override
    public void onModuleLoaded(@NonNull ModuleLoadedParam param) {
        logMsg(TAG + ": module loaded in process: " + param.getProcessName());
        
        // Broadcast status to the app
        if (SYSTEMUI_PACKAGE.equals(param.getProcessName())) {
             // We'll wait until we have a context in onPackageLoaded or onAttachedToWindow
             // Actually, we can't easily send broadcast from onModuleLoaded without a context.
        }

        if (param.getProcessName().equals("dev.module.betterstatusbar")) {
            try {
                Method isModuleActive = ModuleStatusChecker.class.getDeclaredMethod("isModuleActive");
                Method getModuleApiVersion = ModuleStatusChecker.class.getDeclaredMethod("getModuleApiVersion");
                Method getModuleFramework = ModuleStatusChecker.class.getDeclaredMethod("getModuleFramework");
                Method getModuleFrameworkVersion = ModuleStatusChecker.class.getDeclaredMethod("getModuleFrameworkVersion");

                deoptimize(isModuleActive);
                deoptimize(getModuleApiVersion);
                deoptimize(getModuleFramework);
                deoptimize(getModuleFrameworkVersion);

                hook(isModuleActive).intercept(chain -> true);
                hook(getModuleApiVersion).intercept(chain -> getApiVersion());
                hook(getModuleFramework).intercept(chain -> getFrameworkName());
                hook(getModuleFrameworkVersion).intercept(chain -> getFrameworkVersion());
                logMsg(TAG + ": self-hooked ModuleStatusChecker with deoptimization");
            } catch (Throwable t) {
                logErr(TAG + ": self-hook failed", t);
            }
        }
    }

    @Override
    public void onPackageLoaded(@NonNull PackageLoadedParam lp) {
        logMsg(TAG + ": package loaded: " + lp.getPackageName());
        if (lp.getPackageName().equals("dev.module.betterstatusbar")) {
            return;
        }

        if (!SYSTEMUI_PACKAGE.equals(lp.getPackageName())) return;

        logMsg(TAG + ": loading in SystemUI");

        hookTouchTarget(PHONE_STATUS_BAR_VIEW, "onTouchEvent",
                lp.getDefaultClassLoader(), true);
        hookTouchTarget(SHADE_WINDOW_CLASS, "dispatchTouchEvent",
                lp.getDefaultClassLoader(), false);
        hookAttachedToWindow(PHONE_STATUS_BAR_VIEW,
                lp.getDefaultClassLoader());
    }

    private void hookAttachedToWindow(String className, ClassLoader classLoader) {
        try {
            Class<?> cls = Class.forName(className, false, classLoader);
            Method target = cls.getDeclaredMethod("onAttachedToWindow");
            deoptimize(target);
            hook(target).intercept(chain -> {
                Object result = chain.proceed();
                try {
                    View thisView = (View) chain.getThisObject();
                    Context ctx = (Context) thisView.getClass()
                            .getMethod("getContext").invoke(thisView);
                    if (ctx != null) {
                        if (!mReceiverRegistered) registerPrefsReceiver(ctx);
                        if (mDisplayManager == null) initDisplayResources(ctx);
                    }
                } catch (Throwable t) {
                    logMsg(TAG + ": onAttachedToWindow init failed: " + t);
                }
                return result;
            });
            logMsg(TAG + ": hooked " + className + ".onAttachedToWindow");
        } catch (Throwable t) {
            logMsg(TAG + ": failed to hook onAttachedToWindow: " + t);
        }
    }

    private void registerPrefsReceiver(Context context) {
        if (mReceiverRegistered) return;
        mReceiverRegistered = true;

        updatePrefsFromContext(context);
        sendStatusBroadcast(context);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (!Prefs.ACTION_PREFS_CHANGED.equals(intent.getAction())) return;
                boolean prevGesture = mGestureEnabled;
                
                mGestureEnabled = intent.getBooleanExtra(Prefs.KEY_GESTURE_ENABLED, true);
                mOverlayEnabled  = intent.getBooleanExtra(Prefs.KEY_OVERLAY_ENABLED,  true);
                mRelativeEnabled = intent.getBooleanExtra(Prefs.KEY_RELATIVE_BRIGHTNESS, false);
                mLoggingEnabled = intent.getBooleanExtra(Prefs.KEY_LOGGING_ENABLED, false);
                mHapticIntensity = intent.getIntExtra(Prefs.KEY_HAPTIC_INTENSITY, Prefs.DEFAULT_HAPTIC_INTENSITY);
                mSwipeSensitivity = intent.getIntExtra(Prefs.KEY_SWIPE_SENSITIVITY, Prefs.DEFAULT_SWIPE_SENSITIVITY);

                // Update Actions
                updateActionsFromIntent(intent);

                logMsg(TAG + ": prefs updated via broadcast");
                if (prevGesture && !mGestureEnabled && mIndicatorAttached) {
                    hideIndicator();
                }
            }
        };

        IntentFilter filter = new IntentFilter(Prefs.ACTION_PREFS_CHANGED);
        context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
        logMsg(TAG + ": prefs receiver registered");
    }

    private void updatePrefsFromContext(Context context) {
        try {
            mGestureEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    Prefs.KEY_GESTURE_ENABLED, Prefs.DEFAULT_GESTURE_ENABLED) == 1;
            mOverlayEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    Prefs.KEY_OVERLAY_ENABLED, Prefs.DEFAULT_OVERLAY_ENABLED) == 1;
            mRelativeEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    Prefs.KEY_RELATIVE_BRIGHTNESS, Prefs.DEFAULT_RELATIVE_BRIGHTNESS) == 1;
            mLoggingEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    Prefs.KEY_LOGGING_ENABLED, Prefs.DEFAULT_LOGGING_ENABLED) == 1;
            mHapticIntensity = Settings.Secure.getInt(context.getContentResolver(),
                    Prefs.KEY_HAPTIC_INTENSITY, Prefs.DEFAULT_HAPTIC_INTENSITY);
            mSwipeSensitivity = Settings.Secure.getInt(context.getContentResolver(),
                    Prefs.KEY_SWIPE_SENSITIVITY, Prefs.DEFAULT_SWIPE_SENSITIVITY);

            updateActionsFromSecureSettings(context);
        } catch (Throwable t) {
            logMsg(TAG + ": updatePrefsFromContext failed: " + t);
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
                                 ClassLoader classLoader,
                                 final boolean isStatusBarView) {
        try {
            Class<?> cls = Class.forName(className, false, classLoader);
            Method target = cls.getDeclaredMethod(methodName, MotionEvent.class);
            deoptimize(target);
            hook(target).intercept(chain -> {
                MotionEvent ev = (MotionEvent) chain.getArgs().get(0);
                if (ev == null) return chain.proceed();
                
                View thisView = (View) chain.getThisObject();
                if (mDisplayManager == null) {
                    try {
                        Context ctx = (Context) thisView.getClass()
                                .getMethod("getContext").invoke(thisView);
                        if (ctx != null) initDisplayResources(ctx);
                    } catch (Throwable t) {
                        logMsg(TAG + ": display init failed: " + t);
                    }
                }

                if (handleTouchEvent(ev, isStatusBarView, thisView)) {
                    return true;
                }
                return chain.proceed();
            });
            logMsg(TAG + ": hooked " + className + "." + methodName);
        } catch (ClassNotFoundException e) {
            logMsg(TAG + ": class not found: " + className);
        } catch (NoSuchMethodException e) {
            logMsg(TAG + ": method not found: " + methodName);
        } catch (Throwable t) {
            logMsg(TAG + ": hook failed for " + className + ": " + t);
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
                logMsg(TAG + ": found BrightnessUtils.convertLinearToGammaFloat");
            } catch (Throwable t) {
                logMsg(TAG + ": BrightnessUtils not found, using fallback");
            }

            if (mGestureDetector == null) {
                mGestureDetector = new StatusBarGestureDetector(context, (view, ev, type) -> {
                    View tapped = findTappedView(view, ev.getX(), ev.getY());
                    mActionManager.handleGesture(context, tapped, type);
                    if (mLoggingEnabled) {
                        sendLog(context, "Gesture: " + type + " on " + tapped.getClass().getSimpleName());
                    }
                });
            }

            readBrightnessRange();
            initIndicator(context);

            logMsg(TAG + ": display init complete");
        } catch (Throwable t) {
            logMsg(TAG + ": initDisplayResources failed: " + t);
        }
    }

    private void sendStatusBroadcast(Context context) {
        try {
            Intent intent = new Intent(Prefs.ACTION_MODULE_STATUS);
            intent.setPackage("dev.module.betterstatusbar");
            intent.putExtra(Prefs.EXTRA_FRAMEWORK_NAME, getFrameworkName());
            intent.putExtra(Prefs.EXTRA_FRAMEWORK_VERSION, getFrameworkVersion());
            intent.putExtra(Prefs.EXTRA_API_VERSION, getApiVersion());
            context.sendBroadcast(intent);
            logMsg(TAG + ": status broadcast sent from " + context.getPackageName());
        } catch (Throwable t) {
            logErr(TAG + ": failed to send status broadcast", t);
        }
    }

    private void sendLog(Context context, String msg) {
        try {
            Intent intent = new Intent(Prefs.ACTION_GESTURE_LOG);
            intent.setPackage("dev.module.betterstatusbar");
            intent.putExtra(Prefs.EXTRA_LOG_MESSAGE, msg);
            context.sendBroadcast(intent);
        } catch (Throwable ignored) {}
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
            logMsg(TAG + ": readBrightnessRange fallback: " + t);
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
            logMsg(TAG + ": initIndicator failed: " + t);
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
            logMsg(TAG + ": showIndicator failed: " + t);
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
            case MotionEvent.ACTION_MOVE:   return onMove(ev, view);
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

    private boolean onMove(MotionEvent ev, View view) {
        if (!mTouchStartedInStatusBar) return false;
        if (!mGestureEnabled) return false;

        float absDX = Math.abs(ev.getX() - mDownX);
        float absDY = Math.abs(ev.getY() - mDownY);
        if (!mGestureActive) {
            if (absDX <= mGestureSlopPx || absDX <= absDY * HORIZONTAL_RATIO) return false;
            mGestureActive = true;
            mHapticHandler.start(view.getContext(), mDownX, mScreenWidth, mHapticIntensity);
        }

        mHapticHandler.update(view, ev.getX());

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
        float sensitivity = mSwipeSensitivity / 100f;
        if (mRelativeEnabled) {
            return BrightnessCalculator.computeRelativeBrightness(
                    mInitialBrightness, mDownX, fingerX, mScreenWidth, mBrightnessMin, mBrightnessMax, sensitivity);
        } else {
            return BrightnessCalculator.computeAbsoluteBrightness(
                    fingerX, mScreenWidth, mBrightnessMin, mBrightnessMax, sensitivity);
        }
    }

    private void setTemporaryBrightness(float brightness) {
        if (mSetTemporaryBrightnessMethod == null) return;
        try { mSetTemporaryBrightnessMethod.invoke(
                mDisplayManager, Display.DEFAULT_DISPLAY, brightness); }
        catch (Throwable t) { logMsg(TAG + ": setTemporaryBrightness: " + t); }
    }

    private void commitBrightness(float brightness) {
        if (mSetBrightnessMethod == null) return;
        mBgExecutor.execute(() -> {
            try { mSetBrightnessMethod.invoke(
                    mDisplayManager, Display.DEFAULT_DISPLAY, brightness); }
            catch (Throwable t) { logMsg(TAG + ": setBrightness: " + t); }
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
