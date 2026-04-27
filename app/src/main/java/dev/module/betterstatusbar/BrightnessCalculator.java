package dev.module.betterstatusbar;

public class BrightnessCalculator {

    private static final float GAMMA = 2.2f;

    /**
     * Computes brightness based on absolute position on screen.
     */
    public static float computeAbsoluteBrightness(float fingerX, int screenWidth, float min, float max, float sensitivity) {
        // For absolute, we can either ignore sensitivity or use it to scale the active region.
        // Let's keep it simple for now and ignore it for absolute, as absolute implies 1:1.
        float fraction = Math.max(0f, Math.min(1f, fingerX / screenWidth));
        float gammaCorrected = (float) Math.pow(fraction, GAMMA);
        return Math.max(min, Math.min(max, min + gammaCorrected * (max - min)));
    }

    /**
     * Computes brightness based on relative movement from an initial position.
     */
    public static float computeRelativeBrightness(float initialBrightness, float initialX, float currentX, 
                                                  int screenWidth, float min, float max, float sensitivity) {
        float deltaX = currentX - initialX;
        float fractionDelta = (deltaX / screenWidth) * sensitivity;
        
        float initialFraction = (initialBrightness - min) / (max - min);
        // Back-calculate the gamma-corrected fraction (what the user 'sees' as linear)
        float initialGammaFraction = (float) Math.pow(initialFraction, 1.0 / GAMMA);
        
        float newGammaFraction = Math.max(0f, Math.min(1f, initialGammaFraction + fractionDelta));
        float newFraction = (float) Math.pow(newGammaFraction, GAMMA);
        
        return Math.max(min, Math.min(max, min + newFraction * (max - min)));
    }
}
