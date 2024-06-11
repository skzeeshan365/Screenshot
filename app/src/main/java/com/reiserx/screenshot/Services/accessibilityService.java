package com.reiserx.screenshot.Services;

import static com.reiserx.screenshot.Activities.ui.settings.FragmentSensor.SCREENSHOT_TYPE_KEY;
import static com.reiserx.screenshot.Activities.ui.settings.FragmentSensor.SHAKE_COUNT;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.service.notification.StatusBarNotification;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.mlkit.vision.common.InputImage;
import com.reiserx.screenshot.Activities.CaptureActivity;
import com.reiserx.screenshot.Activities.ImageViewerActivity;
import com.reiserx.screenshot.Activities.ui.IconCropView;
import com.reiserx.screenshot.Activities.ui.TextDrawable;
import com.reiserx.screenshot.MachineLearning.OCR;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Receivers.NotificationReceiver;
import com.reiserx.screenshot.Utils.CaptureSound;
import com.reiserx.screenshot.Utils.DataStoreHelper;
import com.reiserx.screenshot.Utils.PhoneUtil;
import com.reiserx.screenshot.Utils.SaveBitmap;
import com.reiserx.screenshot.Utils.ScreenUtil;
import com.reiserx.screenshot.Utils.ScreenshotUtils;
import com.reiserx.screenshot.Utils.ShakeDetector;
import com.reiserx.screenshot.Utils.getLabelFromPackage;

import java.io.File;
import java.util.concurrent.Executor;

public class accessibilityService extends AccessibilityService implements SensorEventListener {
    static String TAG = "AccessibilityTest";
    public static accessibilityService instance;
    private WindowManager windowManager;
    private IconCropView selectionRectView;
    SensorManager sensorManager;
    private SensorManager shakeSensorManager;
    private ShakeDetector shakeDetector;
    Sensor accelerometer;

    public static String ENABLE_NOTIFICATION = "ENABLE_NOTIFICATION";
    public static String ENABLE_SENSOR_PROXIMITY = "ENABLE_SENSOR_PROXIMITY";
    public static String ENABLE_SENSOR_SHAKE = "ENABLE_SENSOR_SHAKE";

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
        DataStoreHelper dataStoreHelper = new DataStoreHelper();
        if (dataStoreHelper.getBooleanValue(ENABLE_NOTIFICATION, false) && !isNotificationActive(8724))
            sendNotification("Capture", "Click to capture screenshot", 8724);
        if (dataStoreHelper.getBooleanValue(ENABLE_SENSOR_PROXIMITY, false))
            enableProximitySensor();
        if (dataStoreHelper.getBooleanValue(ENABLE_SENSOR_SHAKE, false))
            enableShakeDetection();
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
            takeScreenshot(Display.DEFAULT_DISPLAY,
                    getApplicationContext().getMainExecutor(), new TakeScreenshotCallback() {
                        @Override
                        public void onSuccess(@NonNull ScreenshotResult screenshotResult) {
                            Bitmap bitmap = Bitmap.wrapHardwareBuffer(screenshotResult.getHardwareBuffer(), screenshotResult.getColorSpace());

                            SaveBitmap saveBitmap = new SaveBitmap(bitmap, accessibilityService.this);
                            File file = saveBitmap.saveDataLocalDCIM(getLabelFromPackage.getAppLabelFromPackageName(accessibilityService.this, getCurrentForegroundApp()));
                            if (file != null)
                                createScreenshotOverlay(file);
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
                            File file = saveBitmap.saveDataInApp();
                            if (file != null)
                                createScreenshotOverlay(file);
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
            takeScreenshot(Display.DEFAULT_DISPLAY,
                    getApplicationContext().getMainExecutor(), new TakeScreenshotCallback() {
                        @Override
                        public void onSuccess(@NonNull ScreenshotResult screenshotResult) {
                            Bitmap bitmap = Bitmap.wrapHardwareBuffer(screenshotResult.getHardwareBuffer(), screenshotResult.getColorSpace());

                            Bitmap croppedBitmap = cropScreenshot(bitmap, rect);
                            closeSelection();

                            SaveBitmap saveBitmap = new SaveBitmap(croppedBitmap, accessibilityService.this);
                            File file = saveBitmap.saveDataLocalDCIM(getLabelFromPackage.getAppLabelFromPackageName(accessibilityService.this, getCurrentForegroundApp()));
                            if (file != null)
                                createScreenshotOverlay(file);
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
        int cropRight = Math.min(fullBitmap.getWidth(), cropRect.right - borderWidth);

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

        Intent deleteIntent = new Intent(this, NotificationReceiver.class);
        deleteIntent.setAction("NOTIFICATION_DELETED_ACTION");
        PendingIntent pendingIntentDelete = PendingIntent.getBroadcast(this, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

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
                .setOngoing(true)
                .setAutoCancel(false)
                .setDeleteIntent(pendingIntentDelete);

        notificationManager.notify(id, notify_bulder.build());
        DataStoreHelper dataStoreHelper = new DataStoreHelper();
        dataStoreHelper.putBooleanValue(ENABLE_NOTIFICATION, true);
    }

    public void cancelNotification(int id) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
        DataStoreHelper dataStoreHelper = new DataStoreHelper();
        dataStoreHelper.putBooleanValue(ENABLE_NOTIFICATION, false);
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

    public void enableProximitySensor() {
        DataStoreHelper dataStoreHelper = new DataStoreHelper();
        dataStoreHelper.putBooleanValue(ENABLE_SENSOR_PROXIMITY, true);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            if (proximitySensor != null) {
                sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_FASTEST);
            } else {
                Log.e(TAG, "Proximity sensor not available");
            }
        } else {
            Log.e(TAG, "SensorManager not available");
        }
    }

    public void disableProximitySensor() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            sensorManager = null;
        }
        DataStoreHelper dataStoreHelper = new DataStoreHelper();
        dataStoreHelper.putBooleanValue(ENABLE_SENSOR_PROXIMITY, false);
    }

    public void enableShakeDetection() {
        DataStoreHelper StoreHelper = new DataStoreHelper();
        StoreHelper.putBooleanValue(ENABLE_SENSOR_SHAKE, true);

        shakeSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = shakeSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        shakeDetector = new ShakeDetector();
        shakeDetector.setOnShakeListener(count -> {
            DataStoreHelper dataStoreHelper = new DataStoreHelper();
            if (dataStoreHelper.getIntValue(SHAKE_COUNT, 1) == count) {
                if (ScreenUtil.isScreenOn(this)) {
                    // Sensor detected
                    if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == 0) {
                        Intent transparentIntent = new Intent(this, CaptureActivity.class);
                        transparentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        transparentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(transparentIntent);
                    } else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == 1) {
                        takeScreenshots();
                    } else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == 2) {
                        takeScreenshotsSilent();
                    } else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == 3) {
                        CreateSelection();
                    }
                }
            }
        });
        if (accelerometer != null) {
            shakeSensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void disableShakeDetection() {
        if (shakeSensorManager != null) {
            shakeSensorManager.unregisterListener(shakeDetector);
            shakeSensorManager = null;
        }
        DataStoreHelper dataStoreHelper = new DataStoreHelper();
        dataStoreHelper.putBooleanValue(ENABLE_SENSOR_SHAKE, false);
    }

    public boolean isProximitySensorEnabled() {
        return sensorManager != null;
    }

    public boolean isShakeSensorEnabled() {
        return shakeSensorManager != null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        DataStoreHelper dataStoreHelper = new DataStoreHelper();
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float proximityValue = event.values[0];
            Log.d(TAG, String.valueOf(proximityValue));
            if (proximityValue == 0) {
                if (ScreenUtil.isScreenOn(this) || !PhoneUtil.isOnPhoneCall(this)) {
                    // Sensor detected
                    if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == 0) {
                        Intent transparentIntent = new Intent(this, CaptureActivity.class);
                        transparentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        transparentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(transparentIntent);
                    }
                    else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == 1) {
                        takeScreenshots();
                    }
                    else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == 2) {
                        takeScreenshotsSilent();
                    }
                    else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == 3) {
                        CreateSelection();
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disableProximitySensor();
        disableShakeDetection();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        disableProximitySensor();
        disableShakeDetection();
        return super.onUnbind(intent);
    }

    private void createScreenshotOverlay(File file) {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View overlayView = inflater.inflate(R.layout.captured_overlay, null);

        ImageView share_btn = overlayView.findViewById(R.id.imageView1);
        ImageView preview_btn = overlayView.findViewById(R.id.preview_btn);
        ImageView ocr_btn = overlayView.findViewById(R.id.ocr_btn);
        ImageView close_btn = overlayView.findViewById(R.id.close_btn);
        ImageView preview = overlayView.findViewById(R.id.preview);

        share_btn.setImageResource(R.drawable.baseline_share_24);
        preview_btn.setImageResource(R.drawable.baseline_preview_24);
        close_btn.setImageResource(R.drawable.ic_baseline_close_24);
        preview.setImageURI(Uri.fromFile(file));

        TextDrawable textDrawable = new TextDrawable("T");
        textDrawable.setTextColor(getColor(R.color.button_design_text));
        textDrawable.setTextSize(50);
        textDrawable.setFont(this, R.font.source_serif_pro_semibold);
        ocr_btn.setImageDrawable(textDrawable);

        close_btn.setOnClickListener(view -> {
            clearWindowManager(overlayView);
        });

        share_btn.setOnClickListener(view -> {
            ScreenshotUtils screenshotUtils = new ScreenshotUtils(accessibilityService.this);
            screenshotUtils.shareImage(file);
            clearWindowManager(overlayView);
        });

        preview_btn.setOnClickListener(view -> {
            Intent intent = new Intent(this, ImageViewerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("url", file.getAbsolutePath());
            startActivity(intent);
            clearWindowManager(overlayView);
        });

        ocr_btn.setOnClickListener(view -> {
            OCR ocr = new OCR(this);
            InputImage inputImage = ocr.prepareImage(Uri.fromFile(file));
            ocr.scan(inputImage);
            clearWindowManager(overlayView);
        });

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.y = 150;

        windowManager.addView(overlayView, params);

        new Handler(Looper.getMainLooper()).postDelayed(() -> clearWindowManager(overlayView), 5000);
    }

    void clearWindowManager(View view) {
        try {
            if (windowManager != null && view != null) {
                windowManager.removeView(view);
                view = null;
            }
        } catch (Exception e) {

        }
    }
}