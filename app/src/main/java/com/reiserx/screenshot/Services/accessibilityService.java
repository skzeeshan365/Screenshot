package com.reiserx.screenshot.Services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.Display;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.SaveBitmap;

import java.util.concurrent.Executor;

public class accessibilityService extends AccessibilityService {
    static String TAG = "AccessibilityTest";
    public static accessibilityService instance;
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
}