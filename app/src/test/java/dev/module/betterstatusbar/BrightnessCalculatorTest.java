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
        // 0% position should be MIN
        assertEquals(MIN, BrightnessCalculator.computeAbsoluteBrightness(0, SCREEN_WIDTH, MIN, MAX, DEFAULT_SENSITIVITY), DELTA);
        // 100% position should be MAX
        assertEquals(MAX, BrightnessCalculator.computeAbsoluteBrightness(SCREEN_WIDTH, SCREEN_WIDTH, MIN, MAX, DEFAULT_SENSITIVITY), DELTA);
        // 50% position should be roughly 0.217 (0.5^2.2)
        float expected = (float) Math.pow(0.5, 2.2);
        assertEquals(expected, BrightnessCalculator.computeAbsoluteBrightness(SCREEN_WIDTH / 2, SCREEN_WIDTH, MIN, MAX, DEFAULT_SENSITIVITY), DELTA);
    }

    @Test
    public void testRelativeBrightness() {
        // Starting at 50% (initialBrightness is roughly 0.217)
        float initialBrightness = (float) Math.pow(0.5, 2.2);
        float initialX = 200; // arbitrary
        
        // Move 10% of screen width right
        float currentX = initialX + (SCREEN_WIDTH * 0.1f);
        // Should end up at 60% gamma space -> 0.6^2.2
        float expected = (float) Math.pow(0.6, 2.2);
        assertEquals(expected, BrightnessCalculator.computeRelativeBrightness(initialBrightness, initialX, currentX, SCREEN_WIDTH, MIN, MAX, DEFAULT_SENSITIVITY), DELTA);
        
        // Move 10% of screen width left
        currentX = initialX - (SCREEN_WIDTH * 0.1f);
        // Should end up at 40% gamma space -> 0.4^2.2
        expected = (float) Math.pow(0.4, 2.2);
        assertEquals(expected, BrightnessCalculator.computeRelativeBrightness(initialBrightness, initialX, currentX, SCREEN_WIDTH, MIN, MAX, DEFAULT_SENSITIVITY), DELTA);
    }

    @Test
    public void testRelativeBrightnessLimits() {
        float initialBrightness = 0.5f;
        float initialX = 500;
        
        // Move way right
        float currentX = initialX + SCREEN_WIDTH * 2;
        assertEquals(MAX, BrightnessCalculator.computeRelativeBrightness(initialBrightness, initialX, currentX, SCREEN_WIDTH, MIN, MAX, DEFAULT_SENSITIVITY), DELTA);
        
        // Move way left
        currentX = initialX - SCREEN_WIDTH * 2;
        assertEquals(MIN, BrightnessCalculator.computeRelativeBrightness(initialBrightness, initialX, currentX, SCREEN_WIDTH, MIN, MAX, DEFAULT_SENSITIVITY), DELTA);
    }

    @Test
    public void testSensitivity() {
        float initialBrightness = (float) Math.pow(0.5, 2.2);
        float initialX = 500;
        
        // Move 10% of screen width right, but with 200% sensitivity
        float currentX = initialX + (SCREEN_WIDTH * 0.1f);
        float sensitivity = 2.0f;
        // Delta should be treated as 20% -> 50% + 20% = 70% gamma space -> 0.7^2.2
        float expected = (float) Math.pow(0.7, 2.2);
        assertEquals(expected, BrightnessCalculator.computeRelativeBrightness(initialBrightness, initialX, currentX, SCREEN_WIDTH, MIN, MAX, sensitivity), DELTA);
        
        // Move 10% of screen width right, but with 50% sensitivity
        sensitivity = 0.5f;
        // Delta should be treated as 5% -> 50% + 5% = 55% gamma space -> 0.55^2.2
        expected = (float) Math.pow(0.55, 2.2);
        assertEquals(expected, BrightnessCalculator.computeRelativeBrightness(initialBrightness, initialX, currentX, SCREEN_WIDTH, MIN, MAX, sensitivity), DELTA);
    }
}
