package com.reiserx.screenshot.Utils;

import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.core.view.WindowInsetsCompat;

public class SystemUI {
    public static void hideSystemUI(WindowInsetsController controller) {

        if (controller != null) {
            // Hide both the status bar and the navigation bar
            controller.hide(WindowInsets.Type.systemBars());

            // Set the behavior to allow the system bars to be shown with a swipe gesture
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    public static void showSystemUI(WindowInsetsController controller) {
        controller.show(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }
}
