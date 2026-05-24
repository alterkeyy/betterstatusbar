package dev.module.betterstatusbar;

import android.content.Context;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
        Context context = mock(Context.class);
        when(context.getString(R.string.action_none)).thenReturn("None");
        when(context.getString(R.string.action_dark_mode)).thenReturn("Toggle Dark Mode");
        when(context.getString(R.string.action_power_save)).thenReturn("Toggle Power Saving");
        when(context.getString(R.string.action_lock_screen)).thenReturn("Lock Screen");

        assertEquals("None", Prefs.getActionLabel(context, ""));
        assertEquals("Toggle Dark Mode", Prefs.getActionLabel(context, Prefs.ACTION_SYSTEM_DARK_MODE));
        assertEquals("Toggle Power Saving", Prefs.getActionLabel(context, Prefs.ACTION_SYSTEM_POWER_SAVE));
        assertEquals("Lock Screen", Prefs.getActionLabel(context, Prefs.ACTION_SYSTEM_LOCK_SCREEN));
        assertEquals("intent:com.android.settings/.Settings", Prefs.getActionLabel(context, "intent:com.android.settings/.Settings"));
    }

    @Test
    public void testLanguageKeyDefined() {
        assertNotNull(Prefs.KEY_LANGUAGE);
        assertEquals("sbbrightness_language", Prefs.KEY_LANGUAGE);
    }
}
