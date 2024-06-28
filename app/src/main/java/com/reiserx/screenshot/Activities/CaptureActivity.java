package com.reiserx.screenshot.Activities;

import static com.reiserx.screenshot.Activities.ui.settings.SettingsFragment.SCREENSHOT;
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
import com.reiserx.screenshot.Utils.ScreenshotTrigger;
import com.reiserx.screenshot.Utils.isAccessibilityEnabled;
import com.reiserx.screenshot.databinding.CaptureDialogBinding;

public class CaptureActivity extends AppCompatActivity {

    NativeAds nativeAds;
    CaptureDialogBinding binding;
    AlertDialog dialog;
    int REQUEST_CODE;
    ScreenshotTrigger screenshotTrigger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CaptureDialogBinding.inflate(getLayoutInflater());
        dialog();

        screenshotTrigger = new ScreenshotTrigger(this);

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
                binding.fabPartial.setOnClickListener(view -> {
                    REQUEST_CODE = ScreenshotTrigger.CURRENT_WINDOW;
                    finish();
                });
            } else {
                binding.fabPartial.hide();
                binding.textView19.setVisibility(View.GONE);
            }

            binding.fabFull.setOnClickListener(view -> {
                REQUEST_CODE = ScreenshotTrigger.SCREENSHOT;
                finish();
            });

            binding.fabSilent.setOnClickListener(view -> {
                REQUEST_CODE = ScreenshotTrigger.SILENT_SCREENSHOT;
                finish();
            });

            binding.fabSnapshot.setOnClickListener(view -> {
                REQUEST_CODE = ScreenshotTrigger.SNAPSHOT;
                finish();
            });

            binding.fabCopy.setOnClickListener(view -> {
                REQUEST_CODE = ScreenshotTrigger.SNAPSHOT_TYPE_OCR;
                finish();
            });

            binding.fabAi.setOnClickListener(view -> {
                REQUEST_CODE = ScreenshotTrigger.SNAPSHOT_TYPE_AI;
                finish();
            });

            binding.imageView3.setOnClickListener(view -> finish());
        } else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.SCREENSHOT) {
            REQUEST_CODE = ScreenshotTrigger.SCREENSHOT;
            finish();
        } else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.SILENT_SCREENSHOT) {
            REQUEST_CODE = ScreenshotTrigger.SILENT_SCREENSHOT;
            finish();
        } else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.SNAPSHOT) {
            REQUEST_CODE = ScreenshotTrigger.SNAPSHOT;
            finish();
        } else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.SNAPSHOT_TYPE_OCR) {
            REQUEST_CODE = ScreenshotTrigger.SNAPSHOT_TYPE_OCR;
            finish();
        } else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.SNAPSHOT_TYPE_AI) {
            REQUEST_CODE = ScreenshotTrigger.SNAPSHOT_TYPE_AI;
            finish();
        } else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SettingsFragment.CURRENT_WINDOW) {
            REQUEST_CODE = ScreenshotTrigger.CURRENT_WINDOW;
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (nativeAds != null)
            nativeAds.destroyAd();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (REQUEST_CODE != -1)
            screenshotTrigger.capture(REQUEST_CODE);
        super.onDestroy();
    }

    void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(binding.getRoot());
        dialog = builder.create();
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