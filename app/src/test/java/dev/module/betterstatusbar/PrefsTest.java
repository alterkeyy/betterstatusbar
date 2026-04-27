package dev.module.betterstatusbar;

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

    @Test
    public void testGetActionLabel() {
        assertEquals("None", Prefs.getActionLabel(""));
        assertEquals("Toggle Dark Mode", Prefs.getActionLabel(Prefs.ACTION_SYSTEM_DARK_MODE));
        assertEquals("Toggle Power Saving", Prefs.getActionLabel(Prefs.ACTION_SYSTEM_POWER_SAVE));
        assertEquals("Lock Screen", Prefs.getActionLabel(Prefs.ACTION_SYSTEM_LOCK_SCREEN));
        assertEquals("intent:com.android.settings/.Settings", Prefs.getActionLabel("intent:com.android.settings/.Settings"));
    }
}
