package dev.module.betterstatusbar;

import org.junit.Test;
import static org.junit.Assert.*;

public class BrightnessCalculatorTest {

    private static final float DELTA = 0.001f;
    private static final int SCREEN_WIDTH = 1000;
    private static final float MIN = 0.0f;
    private static final float MAX = 1.0f;
    private static final float DEFAULT_SENSITIVITY = 1.0f;

    @Test
    public void testAbsoluteBrightness() {
        assertEquals(MIN, BrightnessCalculator.computeAbsoluteBrightness(0, SCREEN_WIDTH, MIN, MAX), DELTA);
        assertEquals(MAX, BrightnessCalculator.computeAbsoluteBrightness(SCREEN_WIDTH, SCREEN_WIDTH, MIN, MAX), DELTA);
        
        // 25% position (0.25 gamma) -> (0.25/0.5)^2 / 12 = 0.25 / 12 = 0.020833
        assertEquals(0.020833f, BrightnessCalculator.computeAbsoluteBrightness(SCREEN_WIDTH * 0.25f, SCREEN_WIDTH, MIN, MAX), DELTA);
        
        // 50% position (0.5 gamma) -> 1.0 / 12 = 0.08333
        assertEquals(0.08333f, BrightnessCalculator.computeAbsoluteBrightness(SCREEN_WIDTH * 0.5f, SCREEN_WIDTH, MIN, MAX), DELTA);
    }

    @Test
    public void testAbsoluteBrightnessClamping() {
        assertEquals(MIN, BrightnessCalculator.computeAbsoluteBrightness(-100, SCREEN_WIDTH, MIN, MAX), DELTA);
        assertEquals(MAX, BrightnessCalculator.computeAbsoluteBrightness(SCREEN_WIDTH * 2, SCREEN_WIDTH, MIN, MAX), DELTA);
    }

    @Test
    public void testRelativeBrightness() {
        // Start at 0.08333 linear (which is 0.5 gamma)
        float initialBrightness = 0.08333f;
        float initialX = 500;
        
        // Move 25% right -> 0.75 gamma
        float currentX = initialX + (SCREEN_WIDTH * 0.25f);
        // gammaToLinear(0.75) = (exp((0.75 - 0.5599) / 0.1788) + 0.2846) / 12 = 0.2649
        float expected = 0.2649f;
        assertEquals(expected, BrightnessCalculator.computeRelativeBrightness(initialBrightness, initialX, currentX, SCREEN_WIDTH, MIN, MAX, DEFAULT_SENSITIVITY), DELTA);
    }

    @Test
    public void testRelativeBrightnessSensitivity() {
        float initialBrightness = 0.25f;
        float initialX = 500;

        float halfSensResult = BrightnessCalculator.computeRelativeBrightness(
                initialBrightness, initialX, initialX + 100, SCREEN_WIDTH, MIN, MAX, 0.5f);
        float fullSensResult = BrightnessCalculator.computeRelativeBrightness(
                initialBrightness, initialX, initialX + 100, SCREEN_WIDTH, MIN, MAX, 1.0f);
        assertTrue("Lower sensitivity should produce smaller change", halfSensResult < fullSensResult);

        float doubleSensResult = BrightnessCalculator.computeRelativeBrightness(
                initialBrightness, initialX, initialX + 100, SCREEN_WIDTH, MIN, MAX, 2.0f);
        assertTrue("Higher sensitivity should produce larger change", fullSensResult < doubleSensResult);
    }

    @Test
    public void testRelativeBrightnessNegativeDelta() {
        float initialBrightness = 0.5f;
        float initialX = 500;
        float currentX = 250;
        float result = BrightnessCalculator.computeRelativeBrightness(
                initialBrightness, initialX, currentX, SCREEN_WIDTH, MIN, MAX, DEFAULT_SENSITIVITY);
        assertTrue("Moving left should decrease brightness", result < initialBrightness);
    }

    @Test
    public void testClamping() {
        float initialBrightness = 0.0f;
        float initialX = 0;
        assertEquals(0.0f, BrightnessCalculator.computeRelativeBrightness(initialBrightness, initialX, -100, SCREEN_WIDTH, MIN, MAX, DEFAULT_SENSITIVITY), DELTA);
        assertEquals(1.0f, BrightnessCalculator.computeRelativeBrightness(initialBrightness, initialX, SCREEN_WIDTH * 2, SCREEN_WIDTH, MIN, MAX, DEFAULT_SENSITIVITY), DELTA);
    }

    @Test
    public void testGammaLinearRoundTrip() {
        for (float x = 0f; x <= 1.0f; x += 0.1f) {
            float linear = BrightnessCalculator.gammaToLinear(x);
            float gamma = BrightnessCalculator.linearToGamma(linear);
            assertEquals("Round trip should be identity for x=" + x, x, gamma, DELTA);
        }
    }
}
