package dev.module.statusbarbrightnessgesture;

import org.junit.Test;
import static org.junit.Assert.*;

public class StatusBarActionManagerTest {

    @Test
    public void testParseActionNull() {
        assertNull(StatusBarActionManager.parseAction(null));
    }

    @Test
    public void testParseActionInvalidPrefix() {
        assertNull(StatusBarActionManager.parseAction("invalid:something"));
    }

    @Test
    public void testParseActionString() {
        StatusBarActionManager.ParsedAction pa = StatusBarActionManager.parseAction("intent:android.intent.action.VIEW");
        assertNotNull(pa);
        assertEquals("android.intent.action.VIEW", pa.intentAction);
        assertNull(pa.pkg);
        assertNull(pa.cls);
        assertNull(pa.systemAction);
        assertTrue(pa.isIntent());
        assertFalse(pa.isSystem());
    }

    @Test
    public void testParseActionComponent() {
        StatusBarActionManager.ParsedAction pa = StatusBarActionManager.parseAction("intent:com.android.settings/.Settings$BatterySaverSettingsActivity");
        assertNotNull(pa);
        assertNull(pa.intentAction);
        assertEquals("com.android.settings", pa.pkg);
        assertEquals(".Settings$BatterySaverSettingsActivity", pa.cls);
        assertNull(pa.systemAction);
        assertTrue(pa.isIntent());
        assertFalse(pa.isSystem());
    }

    @Test
    public void testParseSystemAction() {
        StatusBarActionManager.ParsedAction pa = StatusBarActionManager.parseAction(Prefs.ACTION_SYSTEM_DARK_MODE);
        assertNotNull(pa);
        assertNull(pa.intentAction);
        assertNull(pa.pkg);
        assertNull(pa.cls);
        assertEquals("toggle_dark_mode", pa.systemAction);
        assertFalse(pa.isIntent());
        assertTrue(pa.isSystem());
    }
}
