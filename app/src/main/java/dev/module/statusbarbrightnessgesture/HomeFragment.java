package dev.module.statusbarbrightnessgesture;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final String GITHUB_URL = "https://github.com/kingsrepo/StatusBarBrightnessGesture";
    private static final String TELEGRAM_URL = "https://t.me/example_channel";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        setupStatusCard(view);
        setupStats(view, prefs);
        setupInfoCard(view);
        setupSupportCard(view);

        return view;
    }

    private void setupStatusCard(View view) {
        ImageView statusIcon = view.findViewById(R.id.status_icon);
        TextView statusTitle = view.findViewById(R.id.status_title);
        TextView statusSubtitle = view.findViewById(R.id.status_subtitle);
        TextView statusRibbon = view.findViewById(R.id.status_ribbon);

        boolean active = ModuleStatusChecker.isModuleActive();
        if (active) {
            statusTitle.setText("Activated");
            statusIcon.setImageResource(R.drawable.ic_check_circle);
            statusIcon.setColorFilter(requireContext().getColor(android.R.color.holo_green_dark));
        } else {
            statusTitle.setText("Not Activated");
            statusIcon.setImageResource(R.drawable.ic_home);
            statusIcon.setColorFilter(requireContext().getColor(android.R.color.holo_red_dark));
        }

        String buildType = BuildConfig.DEBUG ? "Debug" : "Release";
        statusSubtitle.setText("v" + BuildConfig.VERSION_NAME + "-" + buildType.toLowerCase());
        statusRibbon.setText(buildType);
    }

    private void setupStats(View view, SharedPreferences prefs) {
        TextView statGestures = view.findViewById(R.id.stat_gestures);
        TextView statHaptics = view.findViewById(R.id.stat_haptics);

        boolean gesturesEnabled = prefs.getInt(Prefs.KEY_GESTURE_ENABLED, Prefs.DEFAULT_GESTURE_ENABLED) == 1;
        statGestures.setText(gesturesEnabled ? "Enabled" : "Disabled");

        int hapticVal = prefs.getInt(Prefs.KEY_HAPTIC_INTENSITY, Prefs.DEFAULT_HAPTIC_INTENSITY);
        String hapticLabel = "Normal";
        if (hapticVal == 0) hapticLabel = "Off";
        else if (hapticVal == 1) hapticLabel = "Subtle";
        else if (hapticVal == 3) hapticLabel = "Strong";
        statHaptics.setText(hapticLabel);
    }

    private void setupInfoCard(View view) {
        setInfoRow(view.findViewById(R.id.info_build_time), "Build Time", 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        
        setInfoRow(view.findViewById(R.id.info_android_version), "Android Version", 
                Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")");

        int lsposedApi = ModuleStatusChecker.getModuleApiVersion();
        setInfoRow(view.findViewById(R.id.info_lsposed_api), "LSPosed API", 
                lsposedApi > 0 ? String.valueOf(lsposedApi) : "Unknown");

        String lsposedFramework = ModuleStatusChecker.getModuleFramework();
        setInfoRow(view.findViewById(R.id.info_lsposed_framework), "LSPosed Framework", 
                lsposedFramework);
        
        setInfoRow(view.findViewById(R.id.info_device_model), "Device Model", 
                Build.MANUFACTURER + " " + Build.MODEL + " (" + Build.DEVICE + ")");
        
        setInfoRow(view.findViewById(R.id.info_architecture), "System Architecture", 
                String.join(", ", Build.SUPPORTED_ABIS));
    }

    private void setInfoRow(View row, String title, String value) {
        ((TextView) row.findViewById(R.id.row_title)).setText(title);
        ((TextView) row.findViewById(R.id.row_value)).setText(value);
    }

    private void setupSupportCard(View view) {
        view.findViewById(R.id.btn_github).setOnClickListener(v -> openUrl(GITHUB_URL));
        view.findViewById(R.id.btn_telegram).setOnClickListener(v -> openUrl(TELEGRAM_URL));
    }

    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception ignored) {}
    }
}