package com.reiserx.screenshot.Services;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import com.reiserx.screenshot.Activities.MainActivity;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.isAccessibilityEnabled;

public class ScreenshotCurrentWindowTile extends TileService {

    public static boolean TILE_ADDED = false;

    @Override
    public void onTileAdded() {
        if (this.getQsTile() != null) {
            Tile tile = this.getQsTile();
            tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.screenshot));
            tile.setLabel(getString(R.string.current_app));
            tile.setState(Tile.STATE_INACTIVE);
            tile.updateTile();
            TILE_ADDED = true;
        }
    }

    @Override
    public void onClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(this);
            if (isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class) && accessibilityService.instance != null) {
                accessibilityService.instance.closeNotifications();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> accessibilityService.instance.takeScreenshotOfWindows(), 1000);
            } else {
                Toast.makeText(this, "Accessibility service is not enabled.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "Screenshot of current app is not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        TILE_ADDED = false;
    }
}