package com.reiserx.screenshot.Services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.reiserx.screenshot.Activities.ui.IconCropView;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.SaveBitmap;

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
                                saveBitmap.saveDataLocalDCIM();
                            }

                            @Override
                            public void onFailure(int i) {
                                Toast.makeText(accessibilityService.this, "Capture failed " + i, Toast.LENGTH_SHORT).show();
                            }
                        });

        } catch (Exception e) {
            Toast.makeText(this, "An error occurred: "+e, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "An error occurred: "+e, Toast.LENGTH_SHORT).show();
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
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
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

                            Rect adjustedRect = new Rect(rect);
                            int[] location = new int[2];
                            selectionRectView.getLocationOnScreen(location);
                            adjustedRect.offset(-location[0], -location[1]);

                            Bitmap croppedBitmap = cropScreenshot(bitmap, adjustedRect);
                            closeSelection();

                            SaveBitmap saveBitmap = new SaveBitmap(croppedBitmap, accessibilityService.this);
                            saveBitmap.saveDataLocalDCIM();
                        }

                        @Override
                        public void onFailure(int i) {
                            Toast.makeText(accessibilityService.this, "Capture failed " + i, Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(this, "An error occurred: "+e, Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap cropScreenshot(Bitmap fullBitmap, Rect cropRect) {
        int statusBarHeight = getStatusBarHeight();
        int navigationBarHeight = getNavigationBarHeight();

        int borderWidth = 5;

        int cropLeft = Math.max(0, cropRect.left + borderWidth);
        int cropTop = Math.max(0, cropRect.top + statusBarHeight);

        int cropTopMain = Math.max(0, cropRect.top + statusBarHeight + navigationBarHeight);

        int cropRight = Math.min(fullBitmap.getWidth(), cropRect.right - borderWidth);
        int cropBottom = Math.min(fullBitmap.getHeight() - navigationBarHeight, cropRect.bottom);

        int cropWidth = Math.max(0, cropRight - cropLeft);
        int cropHeight = Math.max(0, cropBottom - cropTop);

        return Bitmap.createBitmap(fullBitmap, cropLeft, cropTopMain, cropWidth, cropHeight);
    }


    public int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
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
}