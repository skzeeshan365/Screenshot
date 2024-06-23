package com.reiserx.screenshot.Activities;

import static com.reiserx.screenshot.Activities.ui.settings.SettingsFragment.SCREENSHOT_TYPE_KEY;
import static com.reiserx.screenshot.Services.accessibilityService.CAPTURE_SNAPSHOT_AI;
import static com.reiserx.screenshot.Services.accessibilityService.CAPTURE_SNAPSHOT_DEFAULT;
import static com.reiserx.screenshot.Services.accessibilityService.CAPTURE_SNAPSHOT_OCR;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.reiserx.screenshot.Activities.ui.TextDrawable;
import com.reiserx.screenshot.Activities.ui.settings.SettingsFragment;
import com.reiserx.screenshot.Advertisements.NativeAds;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Services.accessibilityService;
import com.reiserx.screenshot.Utils.DataStoreHelper;
import com.reiserx.screenshot.Utils.isAccessibilityEnabled;
import com.reiserx.screenshot.databinding.CaptureDialogBinding;

public class CaptureActivity extends AppCompatActivity {

    NativeAds nativeAds;
    CaptureDialogBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CaptureDialogBinding.inflate(getLayoutInflater());
        dialog();

        DataStoreHelper dataStoreHelper = new DataStoreHelper();
        if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.DEFAULT) {

            TextDrawable textDrawable = new TextDrawable("AI");
            textDrawable.setTextColor(getColor(R.color.white));
            textDrawable.setTextSize(50);
            binding.fabAi.setImageDrawable(textDrawable);

            nativeAds = new NativeAds(this, binding.adPlaceholder);
            nativeAds.loadAdSmall();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                binding.fabPartial.show();
                binding.textView19.setVisibility(View.VISIBLE);
                takeScreenshotOfWindow();
            } else {
                binding.fabPartial.hide();
                binding.textView19.setVisibility(View.GONE);
            }

            binding.fabFull.setOnClickListener(view -> {
                takeScreenshots();
            });

            binding.fabSilent.setOnClickListener(view -> {
                takeScreenshotsSilent();
            });

            binding.fabSnapshot.setOnClickListener(view -> {
                snapshot(CAPTURE_SNAPSHOT_DEFAULT);
            });

            binding.fabCopy.setOnClickListener(view -> {
                snapshot(CAPTURE_SNAPSHOT_OCR);
            });

            binding.fabAi.setOnClickListener(view -> {
                snapshot(CAPTURE_SNAPSHOT_AI);
            });

            binding.imageView3.setOnClickListener(view -> finish());
        } else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.SCREENSHOT) {
            takeScreenshots();
        } else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.SILENT_SCREENSHOT) {
            takeScreenshotsSilent();
        } else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.SNAPSHOT) {
            snapshot(CAPTURE_SNAPSHOT_DEFAULT);
        } else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.SNAPSHOT_TYPE_OCR) {
            snapshot(CAPTURE_SNAPSHOT_OCR);
        } else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.SNAPSHOT_TYPE_AI) {
            snapshot(CAPTURE_SNAPSHOT_AI);
        }
    }

    private boolean isSystemAlertWindowPermissionGranted() {
        return Settings.canDrawOverlays(getApplicationContext());
    }

    private void requestSystemAlertWindowPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nativeAds.destroyAd();
    }

    void takeScreenshots() {
        isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(this);
        if (isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class) && accessibilityService.instance != null) {
            accessibilityService.instance.closeNotifications();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> accessibilityService.instance.takeScreenshots());
        } else {
            Toast.makeText(this, "Accessibility service is not enabled.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        finish();
    }

    void takeScreenshotsSilent() {
        isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(this);
        if (isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class) && accessibilityService.instance != null) {
            accessibilityService.instance.closeNotifications();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> accessibilityService.instance.takeScreenshotsSilent());
        } else {
            Toast.makeText(this, "Accessibility service is not enabled", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        finish();
    }

    void snapshot(int type) {
        isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(this);
        if (isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class) && accessibilityService.instance != null) {
            accessibilityService.instance.closeNotifications();
            if (isSystemAlertWindowPermissionGranted()) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> accessibilityService.instance.CreateSelection(type));
            } else {
                Toast.makeText(this, "Please grant necessary APPEAR ON TOP permission required for this feature", Toast.LENGTH_LONG).show();
                requestSystemAlertWindowPermission();
            }
        } else {
            Toast.makeText(this, "Accessibility service is not enabled.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    void takeScreenshotOfWindow() {
        binding.fabPartial.setOnClickListener(view -> {
            isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(this);
            if (isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class) && accessibilityService.instance != null) {
                if (isSystemAlertWindowPermissionGranted()) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> accessibilityService.instance.takeScreenshotOfWindows());
                } else {
                    Toast.makeText(this, "Please grant necessary APPEAR ON TOP permission required for this feature", Toast.LENGTH_LONG).show();
                    requestSystemAlertWindowPermission();
                }
            } else {
                Toast.makeText(this, "Accessibility service is not enabled.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            finish();
        });
    }

    void dialog() {
        // Create the AlertDialog with the custom view
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(dialogInterface -> {
            finish();
        });

        dialog.show();

        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
    }
}