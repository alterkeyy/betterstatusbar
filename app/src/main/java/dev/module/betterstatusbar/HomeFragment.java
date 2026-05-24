package dev.module.betterstatusbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private static final String GITHUB_URL = "https://github.com/alterkeyy/betterstatusbar";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        setupStats(view, prefs);
        setupVersionInfo(view);
        setupSupportCard(view);

        return view;
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

    private void setupVersionInfo(View view) {
        TextView statusSubtitle = view.findViewById(R.id.status_subtitle);
        String buildType = BuildConfig.DEBUG ? getString(R.string.build_type_debug) : getString(R.string.build_type_release);
        statusSubtitle.setText("v" + BuildConfig.VERSION_NAME + "-" + buildType.toLowerCase());
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
