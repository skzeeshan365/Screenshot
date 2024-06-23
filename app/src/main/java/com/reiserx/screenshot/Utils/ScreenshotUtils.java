package com.reiserx.screenshot.Utils;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;

public class ScreenshotUtils {
    Context context;
    public static final int APP_DATA_DIR = 1;
    public static final int SHARED_STORAGE = 2;
    public static final int UNKNOWN = -1;

    public ScreenshotUtils(Context context) {
        this.context = context;
    }

    public void shareImage(File file) {
        Uri imgUri = FileProvider.getUriForFile(context, "com.reiserx.screenshot.fileprovider", file);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Install https://play.google.com/store/apps/details?id=com.reiserx.screenshot on Google play store for easy screenshots");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            Intent chooserIntent = Intent.createChooser(shareIntent, "Share this with");
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chooserIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shareText(String text) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);

        try {
            Intent chooserIntent = Intent.createChooser(shareIntent, "Share this with");
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chooserIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getFileLocationType(Context context, File file) {
        File appDataDir = context.getFilesDir();
        File sharedStorageDir = Environment.getExternalStorageDirectory();

        if (file.getAbsolutePath().startsWith(appDataDir.getAbsolutePath())) {
            return APP_DATA_DIR;
        } else if (file.getAbsolutePath().startsWith(sharedStorageDir.getAbsolutePath())) {
            return SHARED_STORAGE;
        } else {
            return UNKNOWN;
        }
    }

    public static void handleScreenshotError(int errorCode, Context context) {
        String errorMessage;
        switch (errorCode) {
            case AccessibilityService.ERROR_TAKE_SCREENSHOT_INTERNAL_ERROR:
                errorMessage = "Internal error occurred while capturing screenshot.";
                break;
            case AccessibilityService.ERROR_TAKE_SCREENSHOT_NO_ACCESSIBILITY_ACCESS:
                errorMessage = "Accessibility service does not have permission to capture screenshot.";
                break;
            case AccessibilityService.ERROR_TAKE_SCREENSHOT_INTERVAL_TIME_SHORT:
                errorMessage = "Interval between screenshot captures is too short.";
                break;
            case AccessibilityService.ERROR_TAKE_SCREENSHOT_INVALID_DISPLAY:
                errorMessage = "Invalid display ID provided.";
                break;
            case AccessibilityService.ERROR_TAKE_SCREENSHOT_INVALID_WINDOW:
                errorMessage = "Invalid window ID provided.";
                break;
            default:
                errorMessage = "Unknown error occurred while capturing screenshot.";
                break;
        }
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
