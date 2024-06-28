package com.reiserx.screenshot.Services;

import static com.reiserx.screenshot.Activities.ui.settings.FragmentSensor.SHAKE_COUNT;
import static com.reiserx.screenshot.Activities.ui.settings.SettingsFragment.SCREENSHOT_TYPE_KEY;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.reiserx.screenshot.Activities.AIActivity;
import com.reiserx.screenshot.Activities.CaptureActivity;
import com.reiserx.screenshot.Activities.ImageViewerActivity;
import com.reiserx.screenshot.Activities.OCRActivity;
import com.reiserx.screenshot.Activities.ui.IconCropView;
import com.reiserx.screenshot.Activities.ui.TextDrawable;
import com.reiserx.screenshot.Activities.ui.settings.SettingsFragment;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Receivers.NotificationReceiver;
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

    public static int CAPTURE_SNAPSHOT_DEFAULT = 0;
    public static int CAPTURE_SNAPSHOT_OCR = 1;
    public static int CAPTURE_SNAPSHOT_AI = 2;

    private long lastClickTime = 0;
    private static final long DOUBLE_TAP_TIME_THRESHOLD = 200;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED;
        info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
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
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            if (isDoubleTap(accessibilityEvent)) {
                handleDoubleTap();
            }
        }
        if (String.valueOf(accessibilityEvent.getPackageName()).equals("com.android.systemui")) {
            if (String.valueOf(accessibilityEvent.getContentDescription()).trim().equals("Back")) {
                closeSelection();
            } else if (String.valueOf(accessibilityEvent.getContentDescription()).trim().equals("Home")) {
                closeSelection();
            }
        }
    }

    private void handleDoubleTap() {
        DataStoreHelper dataStoreHelper = new DataStoreHelper();
        if (dataStoreHelper.getBooleanValue(SettingsFragment.DOUBLE_TAP_ENABLE, false))
            initScreenshotCapture();
    }

    private boolean isDoubleTap(AccessibilityEvent event) {
        long firstClickTime = event.getEventTime();
        long timeDifference = firstClickTime - lastClickTime;
        lastClickTime = firstClickTime;
        return timeDifference < DOUBLE_TAP_TIME_THRESHOLD;
    }

    @Override
    public void takeScreenshot(int displayId, @NonNull Executor executor, @NonNull TakeScreenshotCallback callback) {
        super.takeScreenshot(displayId, executor, callback);
    }

    @Override
    public void takeScreenshotOfWindow(int accessibilityWindowId, @NonNull Executor executor, @NonNull TakeScreenshotCallback callback) {
        super.takeScreenshotOfWindow(accessibilityWindowId, executor, callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public void takeScreenshotOfWindows() {
        try {
            takeScreenshotOfWindow(getRootInActiveWindow().getWindowId(),
                    getApplicationContext().getMainExecutor(), new TakeScreenshotCallback() {
                        @Override
                        public void onSuccess(@NonNull ScreenshotResult screenshotResult) {
                            Bitmap bitmap = Bitmap.wrapHardwareBuffer(screenshotResult.getHardwareBuffer(), screenshotResult.getColorSpace());

                            new SaveBitmap(bitmap, accessibilityService.this, saveBitmap1 -> {
                                File file = saveBitmap1.saveDataLocalDCIM(getLabelFromPackage.getAppLabelFromPackageName(accessibilityService.this, getCurrentForegroundApp()));
                                if (file != null) {
                                    createScreenshotOverlay(file);
                                }
                            });
                        }

                        @Override
                        public void onFailure(int i) {
                            ScreenshotUtils.handleScreenshotError(i, accessibilityService.this);
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(this, "An error occurred: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public void takeScreenshots() {
        try {
            takeScreenshot(Display.DEFAULT_DISPLAY,
                    getApplicationContext().getMainExecutor(), new TakeScreenshotCallback() {
                        @Override
                        public void onSuccess(@NonNull ScreenshotResult screenshotResult) {
                            Bitmap bitmap = Bitmap.wrapHardwareBuffer(screenshotResult.getHardwareBuffer(), screenshotResult.getColorSpace());

                            new SaveBitmap(bitmap, accessibilityService.this, saveBitmap -> {
                                Log.d(TAG, String.valueOf(getCurrentForegroundApp()));
                                File file = saveBitmap.saveDataLocalDCIM(getLabelFromPackage.getAppLabelFromPackageName(accessibilityService.this, getCurrentForegroundApp()));
                                if (file != null)
                                    createScreenshotOverlay(file);
                            });
                        }

                        @Override
                        public void onFailure(int i) {
                            ScreenshotUtils.handleScreenshotError(i, accessibilityService.this);
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

                            new SaveBitmap(bitmap, accessibilityService.this, saveBitmap -> {
                                File file = saveBitmap.saveDataInApp();
                                if (file != null)
                                    createScreenshotOverlay(file);
                            });
                        }

                        @Override
                        public void onFailure(int i) {
                            ScreenshotUtils.handleScreenshotError(i, accessibilityService.this);
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(this, "An error occurred: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInterrupt() {

    }

    @SuppressLint("MissingPermission")
    public void closeNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            performGlobalAction(GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE);
        } else {
            sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        }
    }

    public void CreateSelection(int type) {
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


            selectionRectView = new IconCropView(this, type);
            windowManager.addView(selectionRectView, params);
        }
    }

    public void captureSelectedArea(Rect rect, int type) {
        try {
            takeScreenshot(Display.DEFAULT_DISPLAY,
                    getApplicationContext().getMainExecutor(), new TakeScreenshotCallback() {
                        @Override
                        public void onSuccess(@NonNull ScreenshotResult screenshotResult) {
                            Bitmap bitmap = Bitmap.wrapHardwareBuffer(screenshotResult.getHardwareBuffer(), screenshotResult.getColorSpace());

                            Bitmap croppedBitmap = cropScreenshot(bitmap, rect);
                            closeSelection();

                            new SaveBitmap(croppedBitmap, accessibilityService.this, saveBitmap -> {
                                File file = saveBitmap.saveDataLocalDCIM(getLabelFromPackage.getAppLabelFromPackageName(accessibilityService.this, getCurrentForegroundApp()));
                                if (type == CAPTURE_SNAPSHOT_DEFAULT) {
                                    if (file != null)
                                        createScreenshotOverlay(file);
                                } else if (type == CAPTURE_SNAPSHOT_OCR) {
                                    if (file != null) {
                                        Intent intent = new Intent(accessibilityService.this, OCRActivity.class);
                                        intent.setData(Uri.fromFile(file));
                                        intent.putExtra("temp", true);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                } else if (type == CAPTURE_SNAPSHOT_AI) {
                                    if (file != null) {
                                        Intent intent = new Intent(accessibilityService.this, AIActivity.class);
                                        intent.setData(Uri.fromFile(file));
                                        intent.putExtra("temp", true);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(int i) {
                            ScreenshotUtils.handleScreenshotError(i, accessibilityService.this);
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
        int cropHeight = Math.max(0, cropRect.height() - borderWidth * 2);

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
            Log.d(TAG, String.valueOf(count));
            DataStoreHelper dataStoreHelper = new DataStoreHelper();
            if (dataStoreHelper.getIntValue(SHAKE_COUNT, 1) == count) {
                if (ScreenUtil.isScreenOn(this)) {
                    // Sensor detected
                    initScreenshotCapture();
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
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float proximityValue = event.values[0];
            if (proximityValue == 0) {
                if (ScreenUtil.isScreenOn(this) && !PhoneUtil.isOnPhoneCall(this)) {
                    // Sensor detected
                    initScreenshotCapture();
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
        ImageView ai_explain = overlayView.findViewById(R.id.ai_explain);

        share_btn.setImageResource(R.drawable.baseline_share_24);
        preview_btn.setImageResource(R.drawable.baseline_preview_24);
        close_btn.setImageResource(R.drawable.ic_baseline_close_24);
        preview.setImageURI(Uri.fromFile(file));

        TextDrawable textDrawable = new TextDrawable("T");
        textDrawable.setTextColor(getColor(R.color.button_design_text));
        textDrawable.setTextSize(50);
        textDrawable.setFont(this, R.font.source_serif_pro_semibold);
        ocr_btn.setImageDrawable(textDrawable);

        textDrawable = new TextDrawable("AI");
        textDrawable.setTextColor(getColor(R.color.button_design_text));
        textDrawable.setTextSize(50);
        textDrawable.setFont(this, R.font.source_serif_pro_semibold);
        ai_explain.setImageDrawable(textDrawable);

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
            intent.setData(Uri.fromFile(file));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            clearWindowManager(overlayView);
        });

        ocr_btn.setOnClickListener(view -> {
            Intent intent = new Intent(this, OCRActivity.class);
            intent.setData(Uri.fromFile(file));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            clearWindowManager(overlayView);
        });

        ai_explain.setOnClickListener(view -> {
            Intent intent = new Intent(this, AIActivity.class);
            intent.setData(Uri.fromFile(file));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
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

    public void initScreenshotCapture() {
        DataStoreHelper dataStoreHelper = new DataStoreHelper();
        if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.DEFAULT) {
            Intent transparentIntent = new Intent(this, CaptureActivity.class);
            transparentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            transparentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(transparentIntent);
        } else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.SCREENSHOT)
            takeScreenshots();
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.SILENT_SCREENSHOT)
            takeScreenshotsSilent();
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.SNAPSHOT)
            CreateSelection(CAPTURE_SNAPSHOT_DEFAULT);
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.SNAPSHOT_TYPE_OCR)
            CreateSelection(CAPTURE_SNAPSHOT_OCR);
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.SNAPSHOT_TYPE_AI)
            CreateSelection(CAPTURE_SNAPSHOT_AI);
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.CURRENT_WINDOW)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                takeScreenshotOfWindows();
            } else {
                Toast.makeText(this, "Screenshot of current app is not supported on this device", Toast.LENGTH_SHORT).show();
            }
    }
}