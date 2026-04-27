package dev.module.betterstatusbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;

/**
 * Settings UI.
 *
 * Writes toggle state to Settings.Secure — always available from boot,
 * readable by any process including SystemUI, persists across reboots.
 *
 * Requires WRITE_SECURE_SETTINGS — declared in manifest, granted once via ADB:
 *   adb shell pm grant dev.module.betterstatusbar android.permission.WRITE_SECURE_SETTINGS
 */
@SuppressWarnings("deprecation")
public class SettingsActivity extends Activity {

    private int colText;
    private int colTextSecondary;
    private int colSurface;
    private int colBackground;
    private int colDivider;

    private Switch mGestureSw;
    private Switch mOverlaySw;
    private Switch mRelativeSw;

    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Class<?> dc = Class.forName("com.google.android.material.color.DynamicColors");
            dc.getMethod("applyToActivityIfAvailable", Activity.class).invoke(null, this);
        } catch (Throwable ignored) {}

        super.onCreate(savedInstanceState);
        resolveColours();
        
        mPrefs = getSharedPreferences("settings", Context.MODE_PRIVATE);

        float dp = getResources().getDisplayMetrics().density;
        int hPad = (int)(24*dp), vPad = (int)(20*dp);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(colBackground);
        setContentView(scroll);

        scroll.setOnApplyWindowInsetsListener((v, insets) -> {
            v.setPadding(v.getPaddingLeft(), insets.getSystemWindowInsetTop(),
                    v.getPaddingRight(), insets.getSystemWindowInsetBottom());
            return insets;
        });

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(root);

        // ── Header ────────────────────────────────────────────────────────────
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setBackgroundColor(colSurface);
        header.setPadding(hPad, vPad, hPad, vPad);

        TextView title = new TextView(this);
        title.setText("Status Bar Brightness");
        title.setTextSize(22);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(colText);
        header.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Swipe horizontally on the status bar to adjust brightness");
        subtitle.setTextSize(14);
        subtitle.setTextColor(colTextSecondary);
        subtitle.setPadding(0, (int)(6*dp), 0, 0);
        header.addView(subtitle);

        root.addView(header, matchWidth());
        root.addView(divider(dp), matchWidth());

        // ── Toggles ───────────────────────────────────────────────────────────
        mGestureSw = buildToggleRow(root, "Enable gesture",
                "Swipe left to dim, right to brighten",
                Prefs.KEY_GESTURE_ENABLED, Prefs.DEFAULT_GESTURE_ENABLED,
                dp, hPad, vPad);
        root.addView(divider(dp), matchWidth());

        mOverlaySw = buildToggleRow(root, "Show brightness indicator",
                "Displays brightness % while swiping",
                Prefs.KEY_OVERLAY_ENABLED, Prefs.DEFAULT_OVERLAY_ENABLED,
                dp, hPad, vPad);
        root.addView(divider(dp), matchWidth());

        mRelativeSw = buildToggleRow(root, "Relative adjustment",
                "Adjust relative to current value (experimental)",
                Prefs.KEY_RELATIVE_BRIGHTNESS, Prefs.DEFAULT_RELATIVE_BRIGHTNESS,
                dp, hPad, vPad);
        root.addView(divider(dp), matchWidth());

        // ── Haptics & Sensitivity ───────────────────────────────────────────
        buildSliderRow(root, "Haptic Intensity",
                "Tactile feedback strength",
                Prefs.KEY_HAPTIC_INTENSITY, Prefs.DEFAULT_HAPTIC_INTENSITY,
                0, 3, 1f, dp, hPad, vPad);
        root.addView(divider(dp), matchWidth());

        buildSliderRow(root, "Swipe Sensitivity",
                "Gesture responsiveness",
                Prefs.KEY_SWIPE_SENSITIVITY, Prefs.DEFAULT_SWIPE_SENSITIVITY,
                10, 500, 10f, dp, hPad, vPad);
        root.addView(divider(dp), matchWidth());

        // ── Advanced Gestures ────────────────────────────────────────────────
        TextView advHeader = new TextView(this);
        advHeader.setText("Advanced Gestures");
        advHeader.setTextSize(14);
        advHeader.setTypeface(Typeface.DEFAULT_BOLD);
        advHeader.setTextColor(colTextSecondary);
        advHeader.setPadding(hPad, (int)(24*dp), hPad, (int)(8*dp));
        root.addView(advHeader);
        root.addView(divider(dp), matchWidth());

        buildActionRow(root, "Battery Single Tap", Prefs.KEY_BATTERY_SINGLE_TAP_ACTION, Prefs.DEFAULT_ACTION_BATTERY_TAP, dp, hPad, vPad);
        root.addView(divider(dp), matchWidth());
        buildActionRow(root, "Battery Double Tap", Prefs.KEY_BATTERY_DOUBLE_TAP_ACTION, "", dp, hPad, vPad);
        root.addView(divider(dp), matchWidth());
        buildActionRow(root, "Battery Long Tap", Prefs.KEY_BATTERY_LONG_TAP_ACTION, "", dp, hPad, vPad);
        root.addView(divider(dp), matchWidth());

        buildActionRow(root, "Time Single Tap", Prefs.KEY_TIME_SINGLE_TAP_ACTION, Prefs.DEFAULT_ACTION_TIME_TAP, dp, hPad, vPad);
        root.addView(divider(dp), matchWidth());
        buildActionRow(root, "Time Double Tap", Prefs.KEY_TIME_DOUBLE_TAP_ACTION, "", dp, hPad, vPad);
        root.addView(divider(dp), matchWidth());
        buildActionRow(root, "Time Long Tap", Prefs.KEY_TIME_LONG_TAP_ACTION, "", dp, hPad, vPad);
        root.addView(divider(dp), matchWidth());

        buildActionRow(root, "Status Bar Single Tap", Prefs.KEY_STATUSBAR_SINGLE_TAP_ACTION, "", dp, hPad, vPad);
        root.addView(divider(dp), matchWidth());
        buildActionRow(root, "Status Bar Double Tap", Prefs.KEY_STATUSBAR_DOUBLE_TAP_ACTION, "", dp, hPad, vPad);
        root.addView(divider(dp), matchWidth());
        buildActionRow(root, "Status Bar Long Tap", Prefs.KEY_STATUSBAR_LONG_TAP_ACTION, "", dp, hPad, vPad);
        root.addView(divider(dp), matchWidth());

        // ── How to use ────────────────────────────────────────────────────────
        LinearLayout hint = new LinearLayout(this);
        hint.setOrientation(LinearLayout.VERTICAL);
        hint.setBackgroundColor(colSurface);
        hint.setPadding(hPad, vPad, hPad, vPad);

        TextView hintTitle = new TextView(this);
        hintTitle.setText("How to use");
        hintTitle.setTextSize(14);
        hintTitle.setTypeface(Typeface.DEFAULT_BOLD);
        hintTitle.setTextColor(colText);
        hint.addView(hintTitle);

        for (String tip : new String[]{
                "• Swipe right on the status bar to increase brightness",
                "• Swipe left to decrease brightness",
                "• Works with notification shade open or closed",
                "• Works on the lockscreen",
                "• The % indicator matches the system brightness display",
                "• Indicator colour follows your wallpaper accent",
                "• Configure tap actions in Advanced Gestures"}) {
            TextView t = new TextView(this);
            t.setText(tip);
            t.setTextSize(13);
            t.setTextColor(colTextSecondary);
            t.setPadding(0, (int)(5*dp), 0, 0);
            hint.addView(t);
        }
        root.addView(hint, matchWidth());

        TextView note = new TextView(this);
        note.setText("Changes take effect immediately — no reboot needed.");
        note.setTextSize(12);
        note.setTextColor(colTextSecondary);
        note.setGravity(Gravity.CENTER);
        note.setPadding(hPad, (int)(16*dp), hPad, (int)(16*dp));
        root.addView(note, matchWidth());
    }

    private void buildSliderRow(LinearLayout root, String label, String desc, String prefKey, int defaultVal, float min, float max, float step, float dp, int hPad, int vPad) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setBackgroundColor(colSurface);
        row.setPadding(hPad, (int)(12*dp), hPad, (int)(12*dp));

        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setTextSize(16);
        tv.setTextColor(colText);
        row.addView(tv);

        TextView dv = new TextView(this);
        int current = mPrefs.getInt(prefKey, defaultVal);
        dv.setText(desc + ": " + current + (prefKey.equals(Prefs.KEY_SWIPE_SENSITIVITY) ? "%" : ""));
        dv.setTextSize(13);
        dv.setTextColor(colTextSecondary);
        dv.setPadding(0, (int)(3*dp), 0, (int)(8*dp));
        row.addView(dv);

        Slider slider = new Slider(this);
        slider.setValueFrom(min);
        slider.setValueTo(max);
        slider.setStepSize(step);
        slider.setValue((float)current);
        
        slider.addOnChangeListener((s, value, fromUser) -> {
            int val = (int)value;
            mPrefs.edit().putInt(prefKey, val).apply();
            try {
                Settings.Secure.putInt(getContentResolver(), prefKey, val);
            } catch (SecurityException e) {}
            dv.setText(desc + ": " + val + (prefKey.equals(Prefs.KEY_SWIPE_SENSITIVITY) ? "%" : ""));
            sendPrefs();
        });

        row.addView(slider);
        root.addView(row, matchWidth());
    }

    private void buildActionRow(LinearLayout root, String label, String prefKey, String def, float dp, int hPad, int vPad) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setBackgroundColor(colSurface);
        row.setPadding(hPad, (int)(12*dp), hPad, (int)(12*dp));
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setTextSize(16);
        tv.setTextColor(colText);
        row.addView(tv);

        String current = mPrefs.getString(prefKey, def);
        
        TextView dv = new TextView(this);
        dv.setText(Prefs.getActionLabel(current));
        dv.setTextSize(13);
        dv.setTextColor(colTextSecondary);
        dv.setPadding(0, (int)(3*dp), 0, 0);
        row.addView(dv);

        row.setOnClickListener(v -> {
            String[] options = {"None", "Toggle Dark Mode", "Toggle Power Saving", "Lock Screen", "Custom Intent..."};
            String currentVal = mPrefs.getString(prefKey, def);
            
            int checkedItem = 0; // None
            if (currentVal.equals(Prefs.ACTION_SYSTEM_DARK_MODE)) checkedItem = 1;
            else if (currentVal.equals(Prefs.ACTION_SYSTEM_POWER_SAVE)) checkedItem = 2;
            else if (currentVal.equals(Prefs.ACTION_SYSTEM_LOCK_SCREEN)) checkedItem = 3;
            else if (!currentVal.isEmpty()) checkedItem = 4; // Custom Intent

            new MaterialAlertDialogBuilder(this)
                .setTitle("Select Action for " + label)
                .setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
                    if (which == 0) { // None
                        updateAction(prefKey, "", dv);
                        dialog.dismiss();
                    } else if (which == 1) { // Dark Mode
                        updateAction(prefKey, Prefs.ACTION_SYSTEM_DARK_MODE, dv);
                        dialog.dismiss();
                    } else if (which == 2) { // Power Save
                        updateAction(prefKey, Prefs.ACTION_SYSTEM_POWER_SAVE, dv);
                        dialog.dismiss();
                    } else if (which == 3) { // Lock Screen
                        updateAction(prefKey, Prefs.ACTION_SYSTEM_LOCK_SCREEN, dv);
                        dialog.dismiss();
                    } else if (which == 4) { // Custom Intent
                        dialog.dismiss();
                        showCustomIntentDialog(label, prefKey, def, dp, dv);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        });

        root.addView(row, matchWidth());
    }

    private void updateAction(String prefKey, String value, TextView displayView) {
        mPrefs.edit().putString(prefKey, value).apply();
        try {
            Settings.Secure.putString(getContentResolver(), prefKey, value);
        } catch (SecurityException e) {
            Toast.makeText(this, "Permission missing! Run ADB command.", Toast.LENGTH_LONG).show();
        }
        displayView.setText(Prefs.getActionLabel(value));
        sendPrefs();
    }

    private void showCustomIntentDialog(String label, String prefKey, String def, float dp, TextView dv) {
        EditText input = new EditText(this);
        String currentVal = mPrefs.getString(prefKey, def);
        if (currentVal.isEmpty() || currentVal.startsWith("system:")) {
            currentVal = "intent:";
        }
        input.setText(currentVal);
        input.setHint("intent:pkg/.Activity");
        
        LinearLayout container = new LinearLayout(this);
        container.setPadding((int)(24*dp), (int)(16*dp), (int)(24*dp), 0);
        container.addView(input, matchWidth());

        new MaterialAlertDialogBuilder(this)
            .setTitle("Set Custom Intent for " + label)
            .setMessage("Enter an intent string in the format:\nintent:pkg/.Activity\nOR\nintent:action.NAME")
            .setView(container)
            .setPositiveButton("Save", (dialog, which) -> {
                String val = input.getText().toString().trim();
                
                if (val.isEmpty()) {
                    updateAction(prefKey, "", dv);
                    return;
                }

                if (!val.startsWith("intent:") || val.length() <= 7) {
                    Toast.makeText(this, "Invalid format! Must start with 'intent:'", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateAction(prefKey, val, dv);
            })
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Clear", (dialog, which) -> {
                updateAction(prefKey, "", dv);
            })
            .show();
    }

    private Switch buildToggleRow(LinearLayout root, String titleText, String descText,
                                String prefKey, int defaultVal,
                                float dp, int hPad, int vPad) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackgroundColor(colSurface);
        row.setPadding(hPad, vPad, hPad, vPad);
        row.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout textCol = new LinearLayout(this);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tv = new TextView(this);
        tv.setText(titleText);
        tv.setTextSize(16);
        tv.setTextColor(colText);
        textCol.addView(tv);

        TextView dv = new TextView(this);
        dv.setText(descText);
        dv.setTextSize(13);
        dv.setTextColor(colTextSecondary);
        dv.setPadding(0, (int)(3*dp), 0, 0);
        textCol.addView(dv);

        row.addView(textCol);

        Switch sw = new Switch(this);
        int current = mPrefs.getInt(prefKey, defaultVal);
        sw.setChecked(current == 1);
        sw.setOnCheckedChangeListener((CompoundButton b, boolean checked) -> {
            mPrefs.edit().putInt(prefKey, checked ? 1 : 0).apply();
            try {
                Settings.Secure.putInt(getContentResolver(), prefKey, checked ? 1 : 0);
            } catch (SecurityException e) {
                // ignore
            }
            sendPrefs();
        });
        row.addView(sw);
        row.setOnClickListener(v -> sw.toggle());

        root.addView(row, matchWidth());
        return sw;
    }

    private void sendPrefs() {
        Intent intent = new Intent(Prefs.ACTION_PREFS_CHANGED);
        intent.setPackage("com.android.systemui");
        
        boolean gesture = mGestureSw != null ? mGestureSw.isChecked() : 
                mPrefs.getInt(Prefs.KEY_GESTURE_ENABLED, Prefs.DEFAULT_GESTURE_ENABLED) == 1;
        boolean overlay = mOverlaySw != null ? mOverlaySw.isChecked() :
                mPrefs.getInt(Prefs.KEY_OVERLAY_ENABLED, Prefs.DEFAULT_OVERLAY_ENABLED) == 1;
        boolean relative = mRelativeSw != null ? mRelativeSw.isChecked() :
                mPrefs.getInt(Prefs.KEY_RELATIVE_BRIGHTNESS, Prefs.DEFAULT_RELATIVE_BRIGHTNESS) == 1;

        intent.putExtra(Prefs.KEY_GESTURE_ENABLED, gesture);
        intent.putExtra(Prefs.KEY_OVERLAY_ENABLED, overlay);
        intent.putExtra(Prefs.KEY_RELATIVE_BRIGHTNESS, relative);
        
        intent.putExtra(Prefs.KEY_HAPTIC_INTENSITY, mPrefs.getInt(Prefs.KEY_HAPTIC_INTENSITY, Prefs.DEFAULT_HAPTIC_INTENSITY));
        intent.putExtra(Prefs.KEY_SWIPE_SENSITIVITY, mPrefs.getInt(Prefs.KEY_SWIPE_SENSITIVITY, Prefs.DEFAULT_SWIPE_SENSITIVITY));

        // Strings
        intent.putExtra(Prefs.KEY_BATTERY_SINGLE_TAP_ACTION, mPrefs.getString(Prefs.KEY_BATTERY_SINGLE_TAP_ACTION, Prefs.DEFAULT_ACTION_BATTERY_TAP));
        intent.putExtra(Prefs.KEY_BATTERY_DOUBLE_TAP_ACTION, mPrefs.getString(Prefs.KEY_BATTERY_DOUBLE_TAP_ACTION, ""));
        intent.putExtra(Prefs.KEY_BATTERY_LONG_TAP_ACTION,   mPrefs.getString(Prefs.KEY_BATTERY_LONG_TAP_ACTION, ""));

        intent.putExtra(Prefs.KEY_TIME_SINGLE_TAP_ACTION, mPrefs.getString(Prefs.KEY_TIME_SINGLE_TAP_ACTION, Prefs.DEFAULT_ACTION_TIME_TAP));
        intent.putExtra(Prefs.KEY_TIME_DOUBLE_TAP_ACTION, mPrefs.getString(Prefs.KEY_TIME_DOUBLE_TAP_ACTION, ""));
        intent.putExtra(Prefs.KEY_TIME_LONG_TAP_ACTION,   mPrefs.getString(Prefs.KEY_TIME_LONG_TAP_ACTION, ""));
        
        intent.putExtra(Prefs.KEY_STATUSBAR_SINGLE_TAP_ACTION, mPrefs.getString(Prefs.KEY_STATUSBAR_SINGLE_TAP_ACTION, ""));
        intent.putExtra(Prefs.KEY_STATUSBAR_DOUBLE_TAP_ACTION, mPrefs.getString(Prefs.KEY_STATUSBAR_DOUBLE_TAP_ACTION, ""));
        intent.putExtra(Prefs.KEY_STATUSBAR_LONG_TAP_ACTION,   mPrefs.getString(Prefs.KEY_STATUSBAR_LONG_TAP_ACTION, ""));

        sendBroadcast(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendPrefs();
    }

    private void resolveColours() {
        boolean night = (getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        TypedValue tv = new TypedValue();

        if (getTheme().resolveAttribute(android.R.attr.colorBackground, tv, true)
                && tv.type >= TypedValue.TYPE_FIRST_COLOR_INT
                && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            colBackground = tv.data;
        } else {
            colBackground = night ? 0xFF1C1B1F : 0xFFFFFBFE;
        }

        if (getTheme().resolveAttribute(android.R.attr.windowBackground, tv, true)
                && tv.type >= TypedValue.TYPE_FIRST_COLOR_INT
                && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            colSurface = tv.data;
        } else {
            colSurface = colBackground;
        }

        if (getTheme().resolveAttribute(android.R.attr.textColorPrimary, tv, true)
                && tv.type >= TypedValue.TYPE_FIRST_COLOR_INT
                && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            colText = tv.data;
        } else {
            colText = night ? 0xFFE6E1E5 : 0xFF1C1B1F;
        }

        if (getTheme().resolveAttribute(android.R.attr.textColorSecondary, tv, true)
                && tv.type >= TypedValue.TYPE_FIRST_COLOR_INT
                && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            colTextSecondary = tv.data;
        } else {
            colTextSecondary = night ? 0xFFCAC4D0 : 0xFF49454F;
        }

        colDivider = night ? 0x1FFFFFFF : 0x1F000000;
    }

    private View divider(float dp) {
        View v = new View(this);
        v.setBackgroundColor(colDivider);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (int)(1*dp));
        lp.setMarginStart((int)(24*dp));
        v.setLayoutParams(lp);
        return v;
    }

    private LinearLayout.LayoutParams matchWidth() {
        return new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
