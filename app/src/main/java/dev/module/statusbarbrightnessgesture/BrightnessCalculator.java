package dev.module.statusbarbrightnessgesture;

public class BrightnessCalculator {

    private static final float GAMMA = 2.2f;

    /**
     * Computes brightness based on absolute position on screen.
     */
    public static float computeAbsoluteBrightness(float fingerX, int screenWidth, float min, float max) {
        float fraction = Math.max(0f, Math.min(1f, fingerX / screenWidth));
        float gammaCorrected = (float) Math.pow(fraction, GAMMA);
        return Math.max(min, Math.min(max, min + gammaCorrected * (max - min)));
    }

    /**
     * Computes brightness based on relative movement from an initial position.
     */
    public static float computeRelativeBrightness(float initialBrightness, float initialX, float currentX, 
                                                  int screenWidth, float min, float max) {
        // Sensitivity factor: how much of the screen width corresponds to full brightness range.
        // 1.0f means swiping the full width goes from 0 to 100%.
        float sensitivity = 1.0f;
        
        float deltaX = currentX - initialX;
        float fractionDelta = (deltaX / screenWidth) * sensitivity;
        
        // We need to work in gamma-corrected space for the adjustment to feel natural,
        // or just apply the delta linearly and let the user decide.
        // For now, let's apply the delta to the 'linear' fraction.
        
        float initialFraction = (initialBrightness - min) / (max - min);
        // Back-calculate the gamma-corrected fraction (what the user 'sees' as linear)
        float initialGammaFraction = (float) Math.pow(initialFraction, 1.0 / GAMMA);
        
        float newGammaFraction = Math.max(0f, Math.min(1f, initialGammaFraction + fractionDelta));
        float newFraction = (float) Math.pow(newGammaFraction, GAMMA);
        
        return Math.max(min, Math.min(max, min + newFraction * (max - min)));
    }
}
