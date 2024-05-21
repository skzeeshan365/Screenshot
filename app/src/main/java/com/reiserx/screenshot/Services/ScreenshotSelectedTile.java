package com.reiserx.screenshot.Services;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import com.reiserx.screenshot.Activities.MainActivity;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.isAccessibilityEnabled;

public class ScreenshotSelectedTile extends TileService {

    public static boolean TILE_ADDED = false;

    @Override
    public void onTileAdded() {
        if (ScreenshotSelectedTile.this.getQsTile() != null) {
            Tile tile = ScreenshotSelectedTile.this.getQsTile();
            tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.baseline_crop_square_24));
            tile.setLabel(getString(R.string.selected_screenshot_label));
            tile.setState(Tile.STATE_INACTIVE);
            tile.updateTile();
            TILE_ADDED = true;
        }
    }

    @Override
    public void onClick() {
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
    public void onTileRemoved() {
        super.onTileRemoved();
        TILE_ADDED = false;
    }
}
