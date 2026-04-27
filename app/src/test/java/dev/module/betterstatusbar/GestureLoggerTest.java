package dev.module.betterstatusbar;

import android.content.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GestureLoggerTest {

    @Mock
    Context context;

    @Test
    public void testLogging() throws IOException {
        File tempDir = Files.createTempDirectory("test_logs").toFile();
        when(context.getFilesDir()).thenReturn(tempDir);

        String msg = "Test gesture";
        GestureLogger.log(context, msg);

        String logs = GestureLogger.getLogs(context);
        assertTrue(logs.contains(msg));
        assertTrue(logs.contains("|")); // Check timestamp separator

        GestureLogger.clearLogs(context);
        assertEquals("", GestureLogger.getLogs(context));

        // Cleanup
        tempDir.delete();
    }
}