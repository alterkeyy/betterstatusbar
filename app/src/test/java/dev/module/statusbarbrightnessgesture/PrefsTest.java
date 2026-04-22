package dev.module.statusbarbrightnessgesture;

import org.junit.Test;
import static org.junit.Assert.*;

public class PrefsTest {
    @Test
    public void testSystemActionConstantsExist() {
        // These constants are expected to exist in Prefs.java
        assertEquals("system:toggle_dark_mode", Prefs.ACTION_SYSTEM_DARK_MODE);
        assertEquals("system:toggle_power_save", Prefs.ACTION_SYSTEM_POWER_SAVE);
        assertEquals("system:lock_screen", Prefs.ACTION_SYSTEM_LOCK_SCREEN);
    }
}
