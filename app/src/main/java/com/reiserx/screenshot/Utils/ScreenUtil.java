package com.reiserx.screenshot.Utils;

import android.content.Context;
import android.os.PowerManager;

public class ScreenUtil {

    public static boolean isScreenOn(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            return powerManager.isInteractive();
        }
        return false;
    }
}
