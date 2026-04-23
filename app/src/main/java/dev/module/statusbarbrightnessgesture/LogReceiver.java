package dev.module.statusbarbrightnessgesture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LogReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Prefs.ACTION_GESTURE_LOG.equals(intent.getAction())) {
            String msg = intent.getStringExtra(Prefs.EXTRA_LOG_MESSAGE);
            if (msg != null) {
                GestureLogger.log(context, msg);
            }
        }
    }
}