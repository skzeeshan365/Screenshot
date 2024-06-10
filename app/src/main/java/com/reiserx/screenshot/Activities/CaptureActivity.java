package com.reiserx.screenshot.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.reiserx.screenshot.Services.accessibilityService;
import com.reiserx.screenshot.Utils.ButtonDesign;
import com.reiserx.screenshot.Utils.isAccessibilityEnabled;
import com.reiserx.screenshot.databinding.ActivityCaptureBinding;

public class CaptureActivity extends AppCompatActivity {

    ActivityCaptureBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCaptureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ButtonDesign design = new ButtonDesign(this);
        design.setButtonOutlineLight(binding.screenshotBtn);
        design.setButtonOutlineLight(binding.silentScreenshotBtn);
        design.setButtonOutlineLight(binding.snapshotBtn);

        binding.screenshotBtn.setOnClickListener(view -> {
            design.buttonFillLight(binding.screenshotBtn);
            isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(this);
            if (isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class) && accessibilityService.instance != null) {
                accessibilityService.instance.closeNotifications();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> accessibilityService.instance.takeScreenshots(), 1000);
            } else {
                Toast.makeText(this, "Accessibility service is not enabled.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            finish();
        });

        binding.silentScreenshotBtn.setOnClickListener(view -> {
            design.buttonFillLight(binding.silentScreenshotBtn);
            isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(this);
            if (isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class) && accessibilityService.instance != null) {
                accessibilityService.instance.closeNotifications();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> accessibilityService.instance.takeScreenshotsSilent(), 1000);
            } else {
                Toast.makeText(this, "Accessibility service is not enabled", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            finish();
        });

        binding.snapshotBtn.setOnClickListener(view -> {
            design.buttonFillLight(binding.snapshotBtn);
            isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(this);
            if (isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class) && accessibilityService.instance != null) {
                accessibilityService.instance.closeNotifications();
                if (isSystemAlertWindowPermissionGranted()) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> accessibilityService.instance.CreateSelection(), 1000);
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

        binding.imageView3.setOnClickListener(view -> finish());
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
}