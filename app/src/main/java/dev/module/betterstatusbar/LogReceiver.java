package dev.module.betterstatusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LogReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Prefs.ACTION_GESTURE_LOG.equals(action)) {
            String msg = intent.getStringExtra(Prefs.EXTRA_LOG_MESSAGE);
            if (msg != null) {
                GestureLogger.log(context, msg);
            }
        } else if (Prefs.ACTION_MODULE_STATUS.equals(action)) {
            context.getSharedPreferences(Prefs.LOCAL_PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putLong(Prefs.KEY_STATUS_LAST_SEEN, System.currentTimeMillis())
                    .putString(Prefs.KEY_LAST_FRAMEWORK_NAME, intent.getStringExtra(Prefs.EXTRA_FRAMEWORK_NAME))
                    .putString(Prefs.KEY_LAST_FRAMEWORK_VERSION, intent.getStringExtra(Prefs.EXTRA_FRAMEWORK_VERSION))
                    .putInt(Prefs.KEY_LAST_API_VERSION, intent.getIntExtra(Prefs.EXTRA_API_VERSION, -1))
                    .apply();
        }
    }
}