package dev.module.betterstatusbar;

import android.content.Context;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GestureLogger {
    private static final String TAG = "GestureLogger";
    private static final String LOG_FILE_NAME = "gestures.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());

    public static synchronized void log(Context context, String message) {
        String timestamp = DATE_FORMAT.format(new Date());
        String logEntry = timestamp + " | " + message + "\n";

        File logFile = new File(context.getFilesDir(), LOG_FILE_NAME);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(logEntry);
        } catch (IOException e) {
            Log.e(TAG, "Failed to write log to file", e);
        }
    }

    public static String getLogs(Context context) {
        File logFile = new File(context.getFilesDir(), LOG_FILE_NAME);
        if (!logFile.exists()) return "";

        try {
            java.util.Scanner s = new java.util.Scanner(logFile).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } catch (IOException e) {
            Log.e(TAG, "Failed to read log file", e);
            return "Error reading logs";
        }
    }

    public static void clearLogs(Context context) {
        File logFile = new File(context.getFilesDir(), LOG_FILE_NAME);
        if (logFile.exists()) {
            logFile.delete();
        }
    }
}