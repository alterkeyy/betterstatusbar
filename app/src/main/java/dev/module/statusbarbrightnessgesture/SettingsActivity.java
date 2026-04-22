package dev.module.statusbarbrightnessgesture;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Settings UI.
 *
 * Writes toggle state to Settings.Secure — always available from boot,
 * readable by any process including SystemUI, persists across reboots.
 *
 * Requires WRITE_SECURE_SETTINGS — declared in manifest, granted once via ADB:
 *   adb shell pm grant dev.module.statusbarbrightnessgesture android.permission.WRITE_SECURE_SETTINGS
 *
 * Also sends a broadcast on every change and resume so the hook updates
 * immediately without needing to read Settings.Secure again.
 */
@SuppressWarnings("deprecation")
public class SettingsActivity extends Activity {

    private int colText;
    private int colTextSecondary;
    private int colSurface;
    private int colBackground;
    private int colDivider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Class<?> dc = Class.forName("com.google.android.material.color.DynamicColors");
            dc.getMethod("applyToActivityIfAvailable", Activity.class).invoke(null, this);
        } catch (Throwable ignored) {}

        super.onCreate(savedInstanceState);
        resolveColours();

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
        buildToggleRow(root, "Enable gesture",
                "Swipe left to dim, right to brighten",
                Prefs.KEY_GESTURE_ENABLED, Prefs.DEFAULT_GESTURE_ENABLED,
                dp, hPad, vPad);
        root.addView(divider(dp), matchWidth());

        buildToggleRow(root, "Show brightness indicator",
                "Displays brightness % while swiping",
                Prefs.KEY_OVERLAY_ENABLED, Prefs.DEFAULT_OVERLAY_ENABLED,
                dp, hPad, vPad);
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
                "• Indicator colour follows your wallpaper accent"}) {
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

    private void buildToggleRow(LinearLayout root, String titleText, String descText,
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
        int current = Settings.Secure.getInt(getContentResolver(), prefKey, defaultVal);
        sw.setChecked(current == 1);
        sw.setOnCheckedChangeListener((CompoundButton b, boolean checked) -> {
            try {
                Settings.Secure.putInt(getContentResolver(), prefKey, checked ? 1 : 0);
            } catch (SecurityException e) {
                // Permission not yet granted — toggle still works via broadcast
                // Run: adb shell pm grant dev.module.statusbarbrightnessgesture
                //          android.permission.WRITE_SECURE_SETTINGS
            }
            sendPrefs();
        });
        row.addView(sw);
        row.setOnClickListener(v -> sw.toggle());

        root.addView(row, matchWidth());
    }

    private void sendPrefs() {
        Intent intent = new Intent(Prefs.ACTION_PREFS_CHANGED);
        intent.setPackage("com.android.systemui");
        intent.putExtra(Prefs.KEY_GESTURE_ENABLED,
                Settings.Secure.getInt(getContentResolver(),
                        Prefs.KEY_GESTURE_ENABLED, Prefs.DEFAULT_GESTURE_ENABLED) == 1);
        intent.putExtra(Prefs.KEY_OVERLAY_ENABLED,
                Settings.Secure.getInt(getContentResolver(),
                        Prefs.KEY_OVERLAY_ENABLED, Prefs.DEFAULT_OVERLAY_ENABLED) == 1);
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
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}