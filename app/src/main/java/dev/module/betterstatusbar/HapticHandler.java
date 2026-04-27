package dev.module.betterstatusbar;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.HapticFeedbackConstants;
import android.view.View;

public class HapticHandler {
    private float mLastTickX;
    private float mTickIntervalPx;
    private int mHapticConstant = -1;
    private Vibrator mVibrator;
    private int mIntensity = 0;

    public void start(Context context, float initialX, int screenWidth, int intensity) {
        mLastTickX = initialX;
        mTickIntervalPx = screenWidth / 100f;
        mIntensity = intensity;
        mHapticConstant = mapIntensityToConstant(intensity);
        if (intensity > 0) {
            mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        } else {
            mVibrator = null;
        }
    }

    public void update(View view, float currentX) {
        if (mIntensity == 0) return;

        float delta = Math.abs(currentX - mLastTickX);
        if (delta >= mTickIntervalPx) {
            boolean success = false;
            if (mHapticConstant != -1) {
                success = view.performHapticFeedback(mHapticConstant);
            }
            
            if (!success) {
                performFallbackVibration();
            }
            
            mLastTickX = currentX;
        }
    }

    private void performFallbackVibration() {
        if (mVibrator != null && mVibrator.hasVibrator()) {
            try {
                // Short pulse, intensity-based duration/amplitude
                long duration = 5 + (mIntensity * 5); // 10ms to 20ms
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    int amplitude = 50 + (mIntensity * 50); // 100 to 200
                    mVibrator.vibrate(VibrationEffect.createOneShot(duration, Math.min(255, amplitude)));
                } else {
                    mVibrator.vibrate(duration);
                }
            } catch (Throwable ignored) {}
        }
    }

    private int mapIntensityToConstant(int intensity) {
        switch (intensity) {
            case 1: return HapticFeedbackConstants.CLOCK_TICK;
            case 2: return HapticFeedbackConstants.VIRTUAL_KEY;
            case 3: return HapticFeedbackConstants.LONG_PRESS;
            default: return -1;
        }
    }
}
