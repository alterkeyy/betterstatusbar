package dev.module.betterstatusbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;

public class SettingsFragment extends Fragment {

    private SharedPreferences mPrefs;
    private MaterialSwitch mGestureSw;
    private MaterialSwitch mOverlaySw;
    private MaterialSwitch mRelativeSw;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mPrefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        setupToggles(view);
        setupSliders(view);
        setupActionRows(view);

        return view;
    }

    private void setupToggles(View view) {
        mGestureSw = view.findViewById(R.id.sw_gesture);
        mOverlaySw = view.findViewById(R.id.sw_overlay);
        mRelativeSw = view.findViewById(R.id.sw_relative);

        bindToggle(mGestureSw, Prefs.KEY_GESTURE_ENABLED, Prefs.DEFAULT_GESTURE_ENABLED);
        bindToggle(mOverlaySw, Prefs.KEY_OVERLAY_ENABLED, Prefs.DEFAULT_OVERLAY_ENABLED);
        bindToggle(mRelativeSw, Prefs.KEY_RELATIVE_BRIGHTNESS, Prefs.DEFAULT_RELATIVE_BRIGHTNESS);
    }

    private void bindToggle(MaterialSwitch sw, String key, int def) {
        sw.setChecked(mPrefs.getInt(key, def) == 1);
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int val = isChecked ? 1 : 0;
            mPrefs.edit().putInt(key, val).apply();
            try {
                Settings.Secure.putInt(requireContext().getContentResolver(), key, val);
            } catch (Exception ignored) {}
            sendPrefs();
        });
    }

    private void setupSliders(View view) {
        Slider haptic = view.findViewById(R.id.slider_haptic);
        TextView hapticDesc = view.findViewById(R.id.txt_haptic_val);
        bindSlider(haptic, hapticDesc, "Tactile feedback strength", Prefs.KEY_HAPTIC_INTENSITY, Prefs.DEFAULT_HAPTIC_INTENSITY, "");

        Slider sensitivity = view.findViewById(R.id.slider_sensitivity);
        TextView sensitivityDesc = view.findViewById(R.id.txt_sensitivity_val);
        bindSlider(sensitivity, sensitivityDesc, "Gesture responsiveness", Prefs.KEY_SWIPE_SENSITIVITY, Prefs.DEFAULT_SWIPE_SENSITIVITY, "%");
    }

    private void bindSlider(Slider slider, TextView descView, String descPrefix, String key, int def, String suffix) {
        int current = mPrefs.getInt(key, def);
        slider.setValue((float) current);
        descView.setText(descPrefix + ": " + current + suffix);

        slider.addOnChangeListener((s, value, fromUser) -> {
            int val = (int) value;
            mPrefs.edit().putInt(key, val).apply();
            try {
                Settings.Secure.putInt(requireContext().getContentResolver(), key, val);
            } catch (Exception ignored) {}
            descView.setText(descPrefix + ": " + val + suffix);
            sendPrefs();
        });
    }

    private void setupActionRows(View view) {
        // Battery
        bindActionRow(view.findViewById(R.id.row_battery_single), "Battery Single Tap", Prefs.KEY_BATTERY_SINGLE_TAP_ACTION, Prefs.DEFAULT_ACTION_BATTERY_TAP);
        bindActionRow(view.findViewById(R.id.row_battery_double), "Battery Double Tap", Prefs.KEY_BATTERY_DOUBLE_TAP_ACTION, "");
        bindActionRow(view.findViewById(R.id.row_battery_long),   "Battery Long Tap",   Prefs.KEY_BATTERY_LONG_TAP_ACTION,   "");

        // Time
        bindActionRow(view.findViewById(R.id.row_time_single), "Time Single Tap", Prefs.KEY_TIME_SINGLE_TAP_ACTION, Prefs.DEFAULT_ACTION_TIME_TAP);
        bindActionRow(view.findViewById(R.id.row_time_double), "Time Double Tap", Prefs.KEY_TIME_DOUBLE_TAP_ACTION, "");
        bindActionRow(view.findViewById(R.id.row_time_long),   "Time Long Tap",   Prefs.KEY_TIME_LONG_TAP_ACTION,   "");

        // Status Bar
        bindActionRow(view.findViewById(R.id.row_status_single), "Status Bar Single Tap", Prefs.KEY_STATUSBAR_SINGLE_TAP_ACTION, "");
        bindActionRow(view.findViewById(R.id.row_status_double), "Status Bar Double Tap", Prefs.KEY_STATUSBAR_DOUBLE_TAP_ACTION, "");
        bindActionRow(view.findViewById(R.id.row_status_long),   "Status Bar Long Tap",   Prefs.KEY_STATUSBAR_LONG_TAP_ACTION,   "");
    }

    private void bindActionRow(View row, String label, String key, String def) {
        TextView labelView = row.findViewById(R.id.action_label);
        TextView valView = row.findViewById(R.id.action_value);

        labelView.setText(label);
        String current = mPrefs.getString(key, def);
        valView.setText(Prefs.getActionLabel(current));

        row.setOnClickListener(v -> showActionDialog(label, key, def, valView));
    }

    private void showActionDialog(String label, String key, String def, TextView valView) {
        String[] options = {"None", "Toggle Dark Mode", "Toggle Power Saving", "Lock Screen", "Custom Intent..."};
        String currentVal = mPrefs.getString(key, def);

        int checkedItem = 0;
        if (currentVal.equals(Prefs.ACTION_SYSTEM_DARK_MODE)) checkedItem = 1;
        else if (currentVal.equals(Prefs.ACTION_SYSTEM_POWER_SAVE)) checkedItem = 2;
        else if (currentVal.equals(Prefs.ACTION_SYSTEM_LOCK_SCREEN)) checkedItem = 3;
        else if (!currentVal.isEmpty()) checkedItem = 4;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Action for " + label)
                .setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
                    dialog.dismiss();
                    if (which == 0) updateAction(key, "", valView);
                    else if (which == 1) updateAction(key, Prefs.ACTION_SYSTEM_DARK_MODE, valView);
                    else if (which == 2) updateAction(key, Prefs.ACTION_SYSTEM_POWER_SAVE, valView);
                    else if (which == 3) updateAction(key, Prefs.ACTION_SYSTEM_LOCK_SCREEN, valView);
                    else if (which == 4) showCustomIntentDialog(label, key, def, valView);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCustomIntentDialog(String label, String key, String def, TextView valView) {
        EditText input = new EditText(requireContext());
        String currentVal = mPrefs.getString(key, def);
        if (currentVal.isEmpty() || currentVal.startsWith("system:")) {
            currentVal = "intent:";
        }
        input.setText(currentVal);
        
        LinearLayout container = new LinearLayout(requireContext());
        int pad = (int) (24 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, pad/2, pad, 0);
        container.addView(input, new LinearLayout.LayoutParams(-1, -1));

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Set Custom Intent")
                .setMessage("Format: intent:pkg/.Activity OR intent:action.NAME")
                .setView(container)
                .setPositiveButton("Save", (dialog, which) -> {
                    String val = input.getText().toString().trim();
                    if (val.isEmpty()) {
                        updateAction(key, "", valView);
                    } else if (!val.startsWith("intent:")) {
                        Toast.makeText(requireContext(), "Invalid format!", Toast.LENGTH_SHORT).show();
                    } else {
                        updateAction(key, val, valView);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateAction(String key, String val, TextView valView) {
        mPrefs.edit().putString(key, val).apply();
        try {
            Settings.Secure.putString(requireContext().getContentResolver(), key, val);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Permission missing!", Toast.LENGTH_SHORT).show();
        }
        valView.setText(Prefs.getActionLabel(val));
        sendPrefs();
    }

    private void sendPrefs() {
        Intent intent = new Intent(Prefs.ACTION_PREFS_CHANGED);
        intent.setPackage("com.android.systemui");

        intent.putExtra(Prefs.KEY_GESTURE_ENABLED, mGestureSw.isChecked());
        intent.putExtra(Prefs.KEY_OVERLAY_ENABLED, mOverlaySw.isChecked());
        intent.putExtra(Prefs.KEY_RELATIVE_BRIGHTNESS, mRelativeSw.isChecked());

        intent.putExtra(Prefs.KEY_HAPTIC_INTENSITY, mPrefs.getInt(Prefs.KEY_HAPTIC_INTENSITY, Prefs.DEFAULT_HAPTIC_INTENSITY));
        intent.putExtra(Prefs.KEY_SWIPE_SENSITIVITY, mPrefs.getInt(Prefs.KEY_SWIPE_SENSITIVITY, Prefs.DEFAULT_SWIPE_SENSITIVITY));

        // Simplified for brevity, add all string actions
        String[] keys = {
                Prefs.KEY_BATTERY_SINGLE_TAP_ACTION, Prefs.KEY_BATTERY_DOUBLE_TAP_ACTION, Prefs.KEY_BATTERY_LONG_TAP_ACTION,
                Prefs.KEY_TIME_SINGLE_TAP_ACTION, Prefs.KEY_TIME_DOUBLE_TAP_ACTION, Prefs.KEY_TIME_LONG_TAP_ACTION,
                Prefs.KEY_STATUSBAR_SINGLE_TAP_ACTION, Prefs.KEY_STATUSBAR_DOUBLE_TAP_ACTION, Prefs.KEY_STATUSBAR_LONG_TAP_ACTION
        };
        for (String k : keys) {
            intent.putExtra(k, mPrefs.getString(k, ""));
        }

        requireContext().sendBroadcast(intent);
    }
}