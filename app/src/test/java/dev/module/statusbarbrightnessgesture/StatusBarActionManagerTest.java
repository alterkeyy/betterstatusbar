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
        StatusBarActionManager.ParsedIntent pi = StatusBarActionManager.parseAction("intent:android.intent.action.VIEW");
        assertNotNull(pi);
        assertEquals("android.intent.action.VIEW", pi.action);
        assertNull(pi.pkg);
        assertNull(pi.cls);
    }

    @Test
    public void testParseActionComponent() {
        StatusBarActionManager.ParsedIntent pi = StatusBarActionManager.parseAction("intent:com.android.settings/.Settings$BatterySaverSettingsActivity");
        assertNotNull(pi);
        assertNull(pi.action);
        assertEquals("com.android.settings", pi.pkg);
        assertEquals(".Settings$BatterySaverSettingsActivity", pi.cls);
    }
}
