package com.reiserx.screenshot.Services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.reiserx.screenshot.Activities.CaptureActivity;
import com.reiserx.screenshot.Activities.ui.IconCropView;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.SaveBitmap;
import com.reiserx.screenshot.Utils.getLabelFromPackage;

import java.util.concurrent.Executor;

public class accessibilityService extends AccessibilityService {
    static String TAG = "AccessibilityTest";
    public static accessibilityService instance;
    private WindowManager windowManager;
    private IconCropView selectionRectView;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.flags = AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS | AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        info.notificationTimeout = 100;
        setServiceInfo(info);
        instance = this;
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (String.valueOf(accessibilityEvent.getPackageName()).equals("com.android.systemui")) {
            if (String.valueOf(accessibilityEvent.getContentDescription()).trim().equals("Back")) {
                closeSelection();
            } else if (String.valueOf(accessibilityEvent.getContentDescription()).trim().equals("Home")) {
                closeSelection();
            }
        }
    }

    @Override
    public void takeScreenshot(int displayId, @NonNull Executor executor, @NonNull TakeScreenshotCallback callback) {
        super.takeScreenshot(displayId, executor, callback);
    }

    public void takeScreenshots() {
        try {
            Toast.makeText(this, getString(R.string.local_screenshot_1), Toast.LENGTH_SHORT).show();
            takeScreenshot(Display.DEFAULT_DISPLAY,
                    getApplicationContext().getMainExecutor(), new TakeScreenshotCallback() {
                        @Override
                        public void onSuccess(@NonNull ScreenshotResult screenshotResult) {
                            Bitmap bitmap = Bitmap.wrapHardwareBuffer(screenshotResult.getHardwareBuffer(), screenshotResult.getColorSpace());

                            SaveBitmap saveBitmap = new SaveBitmap(bitmap, accessibilityService.this);
                            saveBitmap.saveDataLocalDCIM(getLabelFromPackage.getAppLabelFromPackageName(accessibilityService.this, getCurrentForegroundApp()));
                        }

                        @Override
                        public void onFailure(int i) {
                            Toast.makeText(accessibilityService.this, "Capture failed " + i, Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(this, "An error occurred: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public void takeScreenshotsSilent() {
        try {
            takeScreenshot(Display.DEFAULT_DISPLAY,
                    getApplicationContext().getMainExecutor(), new TakeScreenshotCallback() {
                        @Override
                        public void onSuccess(@NonNull ScreenshotResult screenshotResult) {
                            Bitmap bitmap = Bitmap.wrapHardwareBuffer(screenshotResult.getHardwareBuffer(), screenshotResult.getColorSpace());

                            SaveBitmap saveBitmap = new SaveBitmap(bitmap, accessibilityService.this);
                            saveBitmap.saveDataInApp();
                        }

                        @Override
                        public void onFailure(int i) {
                            Toast.makeText(accessibilityService.this, "Capture failed " + i, Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(this, "An error occurred: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInterrupt() {

    }

    public void closeNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            performGlobalAction(GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE);
        } else {
            sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        }
    }

    public void CreateSelection() {
        if (windowManager == null) {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }
        if (selectionRectView == null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
            Display display = displayManager.getDisplay(Display.DEFAULT_DISPLAY);
            display.getRealMetrics(displayMetrics);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT, 0, getNavigationBarHeight(),
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);


            selectionRectView = new IconCropView(this);
            windowManager.addView(selectionRectView, params);
        }
    }

    public void captureSelectedArea(Rect rect) {
        try {
            Toast.makeText(this, getString(R.string.local_screenshot_1), Toast.LENGTH_SHORT).show();
            takeScreenshot(Display.DEFAULT_DISPLAY,
                    getApplicationContext().getMainExecutor(), new TakeScreenshotCallback() {
                        @Override
                        public void onSuccess(@NonNull ScreenshotResult screenshotResult) {
                            Bitmap bitmap = Bitmap.wrapHardwareBuffer(screenshotResult.getHardwareBuffer(), screenshotResult.getColorSpace());

                            Bitmap croppedBitmap = cropScreenshot(bitmap, rect);
                            closeSelection();

                            SaveBitmap saveBitmap = new SaveBitmap(croppedBitmap, accessibilityService.this);
                            saveBitmap.saveDataLocalDCIM(getLabelFromPackage.getAppLabelFromPackageName(accessibilityService.this, getCurrentForegroundApp()));
                        }

                        @Override
                        public void onFailure(int i) {
                            Toast.makeText(accessibilityService.this, "Capture failed " + i, Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(this, "An error occurred: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap cropScreenshot(Bitmap fullBitmap, Rect cropRect) {
        int navigationBarHeight = getNavigationBarHeight();

        int borderWidth = 2;

        int cropLeft = Math.max(0, cropRect.left + borderWidth);
        int cropTop = cropRect.top + navigationBarHeight + borderWidth;
        int cropRight = Math.min(fullBitmap.getWidth(), cropRect.right - borderWidth);;

        int cropWidth = Math.max(0, cropRight - cropLeft);
        int cropHeight = Math.max(0, cropRect.height() - borderWidth*2);

        return Bitmap.createBitmap(fullBitmap, cropLeft, cropTop, cropWidth, cropHeight);
    }

    public boolean hasSoftNavigationBar() {
        boolean hasMenuKey = ViewConfiguration.get(accessibilityService.this).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        return !hasMenuKey && !hasBackKey;
    }

    public int getNavigationBarHeight() {
        int navigationBarHeight = 0;
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && hasSoftNavigationBar()) {
            navigationBarHeight = resources.getDimensionPixelSize(resourceId);
        }
        return navigationBarHeight;
    }

    public void closeSelection() {
        if (windowManager != null && selectionRectView != null) {
            windowManager.removeView(selectionRectView);
            selectionRectView = null;
        }
    }

    public void sendNotification(String title, String content, int id) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        String channel_id = "capture_screenshot_channel";

        @SuppressLint("WrongConstant")
        NotificationChannel notificationChannel = new NotificationChannel(channel_id, "Capture screenshot", NotificationManager.IMPORTANCE_MIN);
        notificationChannel.setDescription("service");

        notificationManager.createNotificationChannel(notificationChannel);

        Intent transparentIntent = new Intent(this, CaptureActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, transparentIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notify_bulder = new NotificationCompat.Builder(this, channel_id);
        notify_bulder
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.baseline_crop_square_24)
                .setContentText(content)
                .setContentTitle(title)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setContentInfo("info")
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);

        notificationManager.notify(id, notify_bulder.build());
    }

    public void cancelNotification(int id) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    public boolean isNotificationActive(int notificationId) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();

        for (StatusBarNotification notification : activeNotifications) {
            if (notification.getId() == notificationId) {
                return true;
            }
        }

        return false;
    }

    private String getCurrentForegroundApp() {
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        if (rootInActiveWindow == null) {
            Log.d(TAG, "1");
            return null;
        }
        AccessibilityNodeInfo currentNode = rootInActiveWindow;
        while (currentNode != null) {
            if (currentNode.getPackageName() != null) {
                String packageName = currentNode.getPackageName().toString();
                rootInActiveWindow.recycle();
                return packageName;
            }
            AccessibilityNodeInfo parent = currentNode.getParent();
            currentNode.recycle();
            currentNode = parent;
        }

        rootInActiveWindow.recycle();
        return null;
    }
}