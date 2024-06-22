package com.reiserx.screenshot.Activities;

import static com.reiserx.screenshot.Services.accessibilityService.CAPTURE_SNAPSHOT_AI;
import static com.reiserx.screenshot.Services.accessibilityService.CAPTURE_SNAPSHOT_DEFAULT;
import static com.reiserx.screenshot.Services.accessibilityService.CAPTURE_SNAPSHOT_OCR;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.reiserx.screenshot.Activities.ui.TextDrawable;
import com.reiserx.screenshot.Advertisements.NativeAds;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Services.accessibilityService;
import com.reiserx.screenshot.Utils.ButtonDesign;
import com.reiserx.screenshot.Utils.isAccessibilityEnabled;
import com.reiserx.screenshot.databinding.ActivityCaptureBinding;

public class CaptureActivity extends AppCompatActivity {

    ActivityCaptureBinding binding;
    NativeAds nativeAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCaptureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextDrawable textDrawable = new TextDrawable("AI");
        textDrawable.setTextColor(getColor(R.color.button_design_text));
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
        });

        binding.fabSilent.setOnClickListener(view -> {
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
        });

        binding.fabSnapshot.setOnClickListener(view -> {
            isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(this);
            if (isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class) && accessibilityService.instance != null) {
                accessibilityService.instance.closeNotifications();
                if (isSystemAlertWindowPermissionGranted()) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> accessibilityService.instance.CreateSelection(CAPTURE_SNAPSHOT_DEFAULT));
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

        binding.fabCopy.setOnClickListener(view -> {
            isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(this);
            if (isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class) && accessibilityService.instance != null) {
                if (isSystemAlertWindowPermissionGranted()) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> accessibilityService.instance.CreateSelection(CAPTURE_SNAPSHOT_OCR));
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

        binding.fabAi.setOnClickListener(view -> {
            isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(this);
            if (isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class) && accessibilityService.instance != null) {
                if (isSystemAlertWindowPermissionGranted()) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> accessibilityService.instance.CreateSelection(CAPTURE_SNAPSHOT_AI));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nativeAds.destroyAd();
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
}