package dev.module.betterstatusbar;

import android.app.UiModeManager;
import android.content.Context;
import android.os.PowerManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StatusBarActionManagerTest {

    @Mock
    Context mockContext;
    @Mock
    UiModeManager mockUiModeManager;
    @Mock
    PowerManager mockPowerManager;

    StatusBarActionManager manager;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        manager = new StatusBarActionManager();
        when(mockContext.getSystemService(Context.UI_MODE_SERVICE)).thenReturn(mockUiModeManager);
        when(mockContext.getSystemService(Context.POWER_SERVICE)).thenReturn(mockPowerManager);
    }

    @Test
    public void testPerformSystemActionDarkMode() {
        when(mockUiModeManager.getNightMode()).thenReturn(UiModeManager.MODE_NIGHT_NO);
        boolean result = manager.performSystemAction(mockContext, "toggle_dark_mode");
        assertTrue(result);
        verify(mockUiModeManager).setNightMode(UiModeManager.MODE_NIGHT_YES);
    }

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
