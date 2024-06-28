package com.reiserx.screenshot.Utils;

import static com.reiserx.screenshot.Activities.ui.settings.SettingsFragment.SCREENSHOT_TYPE_KEY;
import static com.reiserx.screenshot.Services.accessibilityService.CAPTURE_SNAPSHOT_AI;
import static com.reiserx.screenshot.Services.accessibilityService.CAPTURE_SNAPSHOT_DEFAULT;
import static com.reiserx.screenshot.Services.accessibilityService.CAPTURE_SNAPSHOT_OCR;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.reiserx.screenshot.Activities.MainActivity;
import com.reiserx.screenshot.Services.accessibilityService;

public class ScreenshotTrigger {
    Context context;
    DataStoreHelper dataStoreHelper;

    public static String SCREENSHOT_TYPE_KEY = "SCREENSHOT_TYPE_KEY";
    public static int DEFAULT = 0;
    public static int SCREENSHOT = 1;
    public static int SILENT_SCREENSHOT = 2;
    public static int SNAPSHOT = 3;
    public static int SNAPSHOT_TYPE_OCR = 4;
    public static int SNAPSHOT_TYPE_AI = 5;
    public static int CURRENT_WINDOW = 6;

    public ScreenshotTrigger(Context context) {
        this.context = context;
        dataStoreHelper = new DataStoreHelper();
    }

    public void capture(int requestCode) {
        if (requestCode == SCREENSHOT) {
            takeScreenshots();
        } else if (requestCode == SILENT_SCREENSHOT) {
            takeScreenshotsSilent();
        } else if (requestCode == SNAPSHOT) {
            snapshot(CAPTURE_SNAPSHOT_DEFAULT);
        } else if (requestCode == SNAPSHOT_TYPE_OCR) {
            snapshot(CAPTURE_SNAPSHOT_OCR);
        } else if (requestCode == SNAPSHOT_TYPE_AI) {
            snapshot(CAPTURE_SNAPSHOT_AI);
        } else if (requestCode == CURRENT_WINDOW) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                takeScreenshotOfWindow();
            }
        }
    }

    void takeScreenshots() {
        isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(context);
        if (isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class) && accessibilityService.instance != null) {
            accessibilityService.instance.closeNotifications();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> accessibilityService.instance.takeScreenshots());
        } else {
            Toast.makeText(context, "Accessibility service is not enabled.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    void takeScreenshotsSilent() {
        isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(context);
        if (isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class) && accessibilityService.instance != null) {
            accessibilityService.instance.closeNotifications();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> accessibilityService.instance.takeScreenshotsSilent());
        } else {
            Toast.makeText(context, "Accessibility service is not enabled", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    void snapshot(int type) {
        isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(context);
        if (isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class) && accessibilityService.instance != null) {
            accessibilityService.instance.closeNotifications();
            if (isSystemAlertWindowPermissionGranted()) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> accessibilityService.instance.CreateSelection(type));
            } else {
                Toast.makeText(context, "Please grant necessary APPEAR ON TOP permission required for this feature", Toast.LENGTH_LONG).show();
                requestSystemAlertWindowPermission();
            }
        } else {
            Toast.makeText(context, "Accessibility service is not enabled.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    void takeScreenshotOfWindow() {
            isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(context);
            if (isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class) && accessibilityService.instance != null) {
                if (isSystemAlertWindowPermissionGranted()) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> accessibilityService.instance.takeScreenshotOfWindows(), 1000);
                } else {
                    Toast.makeText(context, "Please grant necessary APPEAR ON TOP permission required for this feature", Toast.LENGTH_LONG).show();
                    requestSystemAlertWindowPermission();
                }
            } else {
                Toast.makeText(context, "Accessibility service is not enabled.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
    }

    private boolean isSystemAlertWindowPermissionGranted() {
        return Settings.canDrawOverlays(context.getApplicationContext());
    }

    private void requestSystemAlertWindowPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
