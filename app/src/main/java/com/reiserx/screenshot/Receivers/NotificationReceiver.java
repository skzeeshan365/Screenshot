package com.reiserx.screenshot.Receivers;

import static com.reiserx.screenshot.Services.accessibilityService.ENABLE_NOTIFICATION;
import static com.reiserx.screenshot.Services.accessibilityService.instance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.reiserx.screenshot.Utils.DataStoreHelper;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("NOTIFICATION_DELETED_ACTION".equals(intent.getAction())) {
            DataStoreHelper dataStoreHelper = new DataStoreHelper();
            if (instance != null) {
                if (dataStoreHelper.getBooleanValue(ENABLE_NOTIFICATION, false) && !instance.isNotificationActive(8724))
                    instance.sendNotification("Capture", "Click to capture screenshot", 8724);
            }
        }
    }
}