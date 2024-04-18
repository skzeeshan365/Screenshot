package com.reiserx.screenshot.Utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class getLabelFromPackage {
    public static String getAppLabelFromPackageName(Context context, String packageName) {
        final PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo app = new ApplicationInfo(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
            return String.valueOf(pm.getApplicationLabel(app));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
