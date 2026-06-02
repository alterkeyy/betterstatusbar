package dev.module.betterstatusbar;

public class BrightnessCalculator {

    // HLG constants for calculation
    private static final float HLG_A = 0.17883277f;
    private static final float HLG_B = 0.28466892f;
    private static final float HLG_C = 0.55991073f;

    static float gammaToLinear(float x) {
        float xNorm;
        if (x <= 0.5f) {
            xNorm = (float) Math.pow(x / 0.5f, 2.0f);
        } else {
            xNorm = (float) Math.exp((x - HLG_C) / HLG_A) + HLG_B;
        }
        return xNorm / 12.0f;
    }

    static float linearToGamma(float x) {
        float xNorm = x * 12.0f;
        if (xNorm <= 1.0f) {
            return (float) Math.sqrt(xNorm) * 0.5f;
        } else {
            return HLG_A * (float) Math.log(xNorm - HLG_B) + HLG_C;
        }
    }

    public static float computeAbsoluteBrightness(float fingerX, int screenWidth, float min, float max) {
        float fraction = Math.max(0f, Math.min(1f, fingerX / screenWidth));
        float linearFraction = gammaToLinear(fraction);
        return Math.max(min, Math.min(max, min + linearFraction * (max - min)));
    }

    public static float computeRelativeBrightness(float initialBrightness, float initialX, float currentX,
                                                  int screenWidth, float min, float max, float sensitivity) {
        float deltaX = currentX - initialX;
        float fractionDelta = (deltaX / screenWidth) * sensitivity;
        
        float initialFraction = (initialBrightness - min) / (max - min);
        float initialGammaFraction = linearToGamma(initialFraction);
        
        float newGammaFraction = Math.max(0f, Math.min(1f, initialGammaFraction + fractionDelta));
        float newLinearFraction = gammaToLinear(newGammaFraction);
        
        return Math.max(min, Math.min(max, min + newLinearFraction * (max - min)));
    }
}
