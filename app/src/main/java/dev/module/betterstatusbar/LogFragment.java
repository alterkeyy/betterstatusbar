package dev.module.betterstatusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;

public class LogFragment extends Fragment {

    private SharedPreferences mPrefs;
    private TextView mLogText;
    private ScrollView mLogScroll;
    private MaterialSwitch mLoggingSw;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshLogs();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        mPrefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        mLogText = view.findViewById(R.id.txt_logs);
        mLogScroll = view.findViewById(R.id.log_scroll);
        mLoggingSw = view.findViewById(R.id.sw_logging);

        mLoggingSw.setChecked(mPrefs.getInt(Prefs.KEY_LOGGING_ENABLED, Prefs.DEFAULT_LOGGING_ENABLED) == 1);
        mLoggingSw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int val = isChecked ? 1 : 0;
            mPrefs.edit().putInt(Prefs.KEY_LOGGING_ENABLED, val).apply();
            try {
                Settings.Secure.putInt(requireContext().getContentResolver(), Prefs.KEY_LOGGING_ENABLED, val);
            } catch (Exception ignored) {}
            sendPrefsUpdate();
        });

        MaterialButton clearBtn = view.findViewById(R.id.btn_clear_logs);
        clearBtn.setOnClickListener(v -> {
            GestureLogger.clearLogs(requireContext());
            refreshLogs();
        });

        refreshLogs();
        return view;
    }

    private void refreshLogs() {
        if (mLogText == null) return;
        String logs = GestureLogger.getLogs(requireContext());
        if (logs.isEmpty()) {
            mLogText.setText(getString(R.string.log_empty));
        } else {
            mLogText.setText(logs);
            mLogScroll.post(() -> mLogScroll.fullScroll(View.FOCUS_DOWN));
        }
    }

    private void sendPrefsUpdate() {
        Intent intent = new Intent(Prefs.ACTION_PREFS_CHANGED);
        intent.setPackage("com.android.systemui");
        // Only need to send logging enabled for live update
        intent.putExtra(Prefs.KEY_LOGGING_ENABLED, mLoggingSw.isChecked());
        requireContext().sendBroadcast(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLogs();
        requireContext().registerReceiver(mReceiver, new IntentFilter(Prefs.ACTION_GESTURE_LOG), Context.RECEIVER_EXPORTED);
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(mReceiver);
    }
}