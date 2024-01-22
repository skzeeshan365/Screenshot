package com.reiserx.screenshot.Repositories;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.reiserx.screenshot.Models.Screenshots;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScreenshotsRepository {
    private final OnGetScreenshotsComplete onGetScreenshotsComplete;
    private final OnGetSilentScreenshotsComplete onGetSilentScreenshotsComplete;
    String TAG = "dfgfdgdfgd";

    public ScreenshotsRepository(OnGetScreenshotsComplete onGetScreenshotsComplete, OnGetSilentScreenshotsComplete onGetSilentScreenshotsComplete) {
        this.onGetScreenshotsComplete = onGetScreenshotsComplete;
        this.onGetSilentScreenshotsComplete = onGetSilentScreenshotsComplete;
    }

    public void getScreenshots(Context context, int limit) {
        List<Screenshots> imagePaths = new ArrayList<>();

        // Define the URI for the Screenshot folder
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Specify the columns to retrieve
        String[] projection = {
                MediaStore.Images.Media.DATA
        };

        // Specify the selection criteria
        String selection = MediaStore.Images.Media.DATA + " like ?";
        String[] selectionArgs = new String[]{"%/DCIM/Screenshots/%"};

        // Specify the sort order by date
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        // Execute the query using ContentResolver
        ContentResolver contentResolver = context.getContentResolver();
        try (Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)) {
            if (cursor != null) {
                int dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                // Iterate through the cursor to get image paths
                int count = 0;
                while (cursor.moveToNext() && count < limit) {
                    String imagePath = cursor.getString(dataColumnIndex);
                    imagePaths.add(new Screenshots(imagePath));
                    count++;
                }
                onGetScreenshotsComplete.onSuccess(imagePaths);
            } else {
                onGetScreenshotsComplete.onFailure("Failed to get screenshots");
            }
        } catch (Exception e) {
            onGetScreenshotsComplete.onFailure("Exception: " + e.getMessage());
        }
    }


    public void getScreenshotsInApp(Context context) {
        List<Screenshots> imagePaths = new ArrayList<>();

        // Specify the directory for the Screenshots in your app's internal storage
        File directory = new File(context.getFilesDir(), "Screenshots");

        // List files directly from your app's directory
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String imagePath = file.getAbsolutePath();
                    Log.d(TAG, "Image path from app's directory: " + imagePath);
                    imagePaths.add(new Screenshots(imagePath));
                }
            }
            onGetSilentScreenshotsComplete.onSilentSuccess(imagePaths);
        } else {
            onGetSilentScreenshotsComplete.onSilentFailure("Failed to get screenshots");
            Log.d(TAG, "No files found in the app's directory");
        }
    }



    public interface OnGetScreenshotsComplete {
        void onSuccess(List<Screenshots> parentItemList);

        void onFailure(String error);
    }

    public interface OnGetSilentScreenshotsComplete {
        void onSilentSuccess(List<Screenshots> parentItemList);

        void onSilentFailure(String error);
    }
}
