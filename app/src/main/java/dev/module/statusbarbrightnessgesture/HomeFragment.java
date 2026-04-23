package dev.module.statusbarbrightnessgesture;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        MaterialCardView statusCard = view.findViewById(R.id.card_status);
        ImageView statusIcon = view.findViewById(R.id.status_icon);
        TextView statusText = view.findViewById(R.id.status_text);

        boolean active = ModuleStatusChecker.isModuleActive();
        if (active) {
            statusText.setText("Active");
            statusIcon.setImageResource(R.drawable.ic_home); // Or a checkmark icon if available
            // statusIcon.setColorFilter(getResources().getColor(android.R.color.holo_green_dark, null));
        } else {
            statusText.setText("Inactive - Module not responding");
            // statusIcon.setColorFilter(getResources().getColor(android.R.color.holo_red_dark, null));
        }

        TextView infoAndroid = view.findViewById(R.id.info_android_version);
        infoAndroid.setText("Android Version: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")");

        TextView infoBrightness = view.findViewById(R.id.info_brightness);
        try {
            int brightness = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            infoBrightness.setText("Current Brightness: " + brightness + " / 255");
        } catch (Exception e) {
            infoBrightness.setText("Current Brightness: Unknown");
        }

        return view;
    }
}