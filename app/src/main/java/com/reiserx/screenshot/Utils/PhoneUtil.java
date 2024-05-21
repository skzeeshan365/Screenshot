package com.reiserx.screenshot.Utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class PhoneUtil {
    public static boolean isOnPhoneCall(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            // Check call state
            return telephonyManager.getCallState() != TelephonyManager.CALL_STATE_IDLE;
        }
        return false;
    }
}
