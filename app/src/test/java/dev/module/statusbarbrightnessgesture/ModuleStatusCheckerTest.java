package dev.module.statusbarbrightnessgesture;

import org.junit.Test;
import static org.junit.Assert.*;

public class ModuleStatusCheckerTest {
    @Test
    public void testIsModuleActiveDefault() {
        // By default, it should be false as it is only hooked to return true in the injected process.
        assertFalse(ModuleStatusChecker.isModuleActive());
    }
}