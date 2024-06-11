package com.reiserx.screenshot.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

public class ScreenshotUtils {
    Context context;

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
}
