package dev.module.statusbarbrightnessgesture;

import org.junit.Test;
import static org.junit.Assert.*;

public class BrightnessCalculatorTest {

    private static final float DELTA = 0.001f;
    private static final int SCREEN_WIDTH = 1000;
    private static final float MIN = 0.0f;
    private static final float MAX = 1.0f;

    @Test
    public void testAbsoluteBrightness() {
        // 0% position should be MIN
        assertEquals(MIN, BrightnessCalculator.computeAbsoluteBrightness(0, SCREEN_WIDTH, MIN, MAX), DELTA);
        // 100% position should be MAX
        assertEquals(MAX, BrightnessCalculator.computeAbsoluteBrightness(SCREEN_WIDTH, SCREEN_WIDTH, MIN, MAX), DELTA);
        // 50% position should be roughly 0.217 (0.5^2.2)
        float expected = (float) Math.pow(0.5, 2.2);
        assertEquals(expected, BrightnessCalculator.computeAbsoluteBrightness(SCREEN_WIDTH / 2, SCREEN_WIDTH, MIN, MAX), DELTA);
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
        assertEquals(expected, BrightnessCalculator.computeRelativeBrightness(initialBrightness, initialX, currentX, SCREEN_WIDTH, MIN, MAX), DELTA);
        
        // Move 10% of screen width left
        currentX = initialX - (SCREEN_WIDTH * 0.1f);
        // Should end up at 40% gamma space -> 0.4^2.2
        expected = (float) Math.pow(0.4, 2.2);
        assertEquals(expected, BrightnessCalculator.computeRelativeBrightness(initialBrightness, initialX, currentX, SCREEN_WIDTH, MIN, MAX), DELTA);
    }

    @Test
    public void testRelativeBrightnessLimits() {
        float initialBrightness = 0.5f;
        float initialX = 500;
        
        // Move way right
        float currentX = initialX + SCREEN_WIDTH * 2;
        assertEquals(MAX, BrightnessCalculator.computeRelativeBrightness(initialBrightness, initialX, currentX, SCREEN_WIDTH, MIN, MAX), DELTA);
        
        // Move way left
        currentX = initialX - SCREEN_WIDTH * 2;
        assertEquals(MIN, BrightnessCalculator.computeRelativeBrightness(initialBrightness, initialX, currentX, SCREEN_WIDTH, MIN, MAX), DELTA);
    }
}
