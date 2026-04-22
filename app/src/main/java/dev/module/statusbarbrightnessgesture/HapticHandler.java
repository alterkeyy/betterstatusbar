package dev.module.statusbarbrightnessgesture;

import android.view.HapticFeedbackConstants;
import android.view.View;

public class HapticHandler {
    private float mLastTickX;
    private float mTickIntervalPx;
    private int mHapticConstant = -1;

    public void start(float initialX, int screenWidth, int intensity) {
        mLastTickX = initialX;
        // 100 ticks across the screen (1% steps)
        mTickIntervalPx = screenWidth / 100f;
        mHapticConstant = mapIntensityToConstant(intensity);
    }

    public void update(View view, float currentX) {
        if (mHapticConstant == -1) return;

        float delta = Math.abs(currentX - mLastTickX);
        if (delta >= mTickIntervalPx) {
            view.performHapticFeedback(mHapticConstant);
            mLastTickX = currentX;
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
