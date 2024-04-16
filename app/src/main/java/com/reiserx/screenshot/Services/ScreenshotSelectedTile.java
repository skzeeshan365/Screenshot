package com.reiserx.screenshot.Services;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.os.Looper;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import com.reiserx.screenshot.Activities.MainActivity;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.isAccessibilityEnabled;

public class ScreenshotSelectedTile extends TileService {
    @Override
    public void onTileAdded() {
        if (ScreenshotSelectedTile.this.getQsTile() != null) {
            Tile tile = ScreenshotSelectedTile.this.getQsTile();
            tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.baseline_crop_square_24));
            tile.setLabel(getString(R.string.selected_screenshot_label));
            tile.setState(Tile.STATE_INACTIVE);
            tile.updateTile();
        }
    }

    @Override
    public void onClick() {
        isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(this);
        if (isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class) && accessibilityService.instance != null) {
            accessibilityService.instance.closeNotifications();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> accessibilityService.instance.CreateSelection(), 1000);
        } else {
            Toast.makeText(this, "Accessibility service is not enabled.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}