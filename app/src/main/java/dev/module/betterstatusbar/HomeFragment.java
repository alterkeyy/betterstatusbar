package dev.module.betterstatusbar;

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

    private static final String GITHUB_URL = "https://github.com/kingsrepo/betterstatusbar";

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

        SharedPreferences statusPrefs = requireContext().getSharedPreferences(Prefs.LOCAL_PREFS_NAME, Context.MODE_PRIVATE);
        long lastSeen = statusPrefs.getLong(Prefs.KEY_STATUS_LAST_SEEN, 0);
        
        // Consider active if seen in the last 24 hours (usually sent on SystemUI start/hook)
        boolean active = (System.currentTimeMillis() - lastSeen) < (24 * 60 * 60 * 1000);
        
        if (active) {
            statusTitle.setText(getString(R.string.status_activated));
            statusIcon.setImageResource(R.drawable.ic_check_circle);
            statusIcon.setColorFilter(requireContext().getColor(android.R.color.holo_green_dark));
        } else {
            statusTitle.setText(getString(R.string.status_not_activated));
            statusIcon.setImageResource(R.drawable.ic_home);
            statusIcon.setColorFilter(requireContext().getColor(android.R.color.holo_red_dark));
        }

        String buildType = BuildConfig.DEBUG ? getString(R.string.build_type_debug) : getString(R.string.build_type_release);
        statusSubtitle.setText("v" + BuildConfig.VERSION_NAME + "-" + buildType.toLowerCase());
    }

    private void setupStats(View view, SharedPreferences prefs) {
        TextView statGestures = view.findViewById(R.id.stat_gestures);
        TextView statHaptics = view.findViewById(R.id.stat_haptics);

        boolean gesturesEnabled = prefs.getInt(Prefs.KEY_GESTURE_ENABLED, Prefs.DEFAULT_GESTURE_ENABLED) == 1;
        statGestures.setText(gesturesEnabled ? getString(R.string.state_enabled) : getString(R.string.state_disabled));

        int hapticVal = prefs.getInt(Prefs.KEY_HAPTIC_INTENSITY, Prefs.DEFAULT_HAPTIC_INTENSITY);
        int hapticLabelRes = R.string.haptic_normal;
        if (hapticVal == 0) hapticLabelRes = R.string.haptic_off;
        else if (hapticVal == 1) hapticLabelRes = R.string.haptic_subtle;
        else if (hapticVal == 3) hapticLabelRes = R.string.haptic_strong;
        statHaptics.setText(getString(hapticLabelRes));
    }

    private void setupInfoCard(View view) {
        setInfoRow(view.findViewById(R.id.info_build_time), getString(R.string.info_build_time), 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        
        setInfoRow(view.findViewById(R.id.info_android_version), getString(R.string.info_android_version), 
                Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")");

        SharedPreferences statusPrefs = requireContext().getSharedPreferences(Prefs.LOCAL_PREFS_NAME, Context.MODE_PRIVATE);
        String framework = statusPrefs.getString(Prefs.KEY_LAST_FRAMEWORK_NAME, getString(R.string.info_not_detected));
        String version = statusPrefs.getString(Prefs.KEY_LAST_FRAMEWORK_VERSION, "");

        setInfoRow(view.findViewById(R.id.info_lsposed_version), getString(R.string.info_framework), 
                framework + (version.isEmpty() ? "" : " " + version));

        int apiVersion = statusPrefs.getInt(Prefs.KEY_LAST_API_VERSION, -1);
        setInfoRow(view.findViewById(R.id.info_lsposed_api), getString(R.string.info_api_version), 
                apiVersion > 0 ? String.valueOf(apiVersion) : getString(R.string.info_not_detected));
        
        setInfoRow(view.findViewById(R.id.info_device_model), getString(R.string.info_device_model), 
                Build.MANUFACTURER + " " + Build.MODEL + " (" + Build.DEVICE + ")");
        
        setInfoRow(view.findViewById(R.id.info_architecture), getString(R.string.info_architecture), 
                String.join(", ", Build.SUPPORTED_ABIS));
    }

    private void setInfoRow(View row, String title, String value) {
        ((TextView) row.findViewById(R.id.row_title)).setText(title);
        ((TextView) row.findViewById(R.id.row_value)).setText(value);
    }

    private void setupSupportCard(View view) {
        view.findViewById(R.id.btn_github).setOnClickListener(v -> openUrl(GITHUB_URL));
    }

    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception ignored) {}
    }
}