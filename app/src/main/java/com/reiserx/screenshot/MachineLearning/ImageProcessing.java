package com.reiserx.screenshot.MachineLearning;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.work.WorkManager;

import com.reiserx.screenshot.Activities.ui.settings.FileFragment;
import com.reiserx.screenshot.Utils.DataStoreHelper;

import java.util.ArrayList;
import java.util.List;

public class ImageProcessing {
    Context context;
    DataStoreHelper dataStoreHelper;
    public static String OFFSET = "IMAGE_PROCESS_OFFSET";
    int lastProcessedIndex;
    public ImageProcessing(Context context) {
        this.context = context;
        dataStoreHelper = new DataStoreHelper();
        lastProcessedIndex = dataStoreHelper.getIntValue(OFFSET, 0);
    }

    public List<String> getScreenshotsInBatches(int batchSize) {
        int startIndex = lastProcessedIndex;

        List<String> screenshotFilePaths = getScreenshotsFromFolder(startIndex, batchSize);

        if (!screenshotFilePaths.isEmpty()) {

            if (screenshotFilePaths.size() < batchSize) {
                lastProcessedIndex = 0;
            } else {
                lastProcessedIndex += batchSize;
            }

            saveLastProcessedIndex();
            return screenshotFilePaths;
        } else {
            lastProcessedIndex = 0;
            screenshotFilePaths = getScreenshotsFromFolder(0, batchSize);

            if (!screenshotFilePaths.isEmpty()) {

                if (screenshotFilePaths.size() < batchSize) {
                    lastProcessedIndex = 0;
                    WorkManager.getInstance(context.getApplicationContext()).cancelAllWorkByTag("label_processing");
                } else {
                    lastProcessedIndex += batchSize;
                }

                saveLastProcessedIndex();
                return screenshotFilePaths;
            } else {
                WorkManager.getInstance(context.getApplicationContext()).cancelAllWorkByTag("label_processing");
                return null;
            }
        }
    }

    private void saveLastProcessedIndex() {
        dataStoreHelper.putIntValue(OFFSET, lastProcessedIndex);
    }

    List<String> getScreenshotsFromFolder(int startIndex, int batchSize) {
        List<String> screenshotFilePaths = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.Media.DATA };
        String selection = MediaStore.Images.Media.DATA + " like ?";
        String[] selectionArgs = new String[]{"%/DCIM/"+dataStoreHelper.getStringValue(FileFragment.DEFAULT_STORAGE_KEY, null)+"/%"};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        ContentResolver contentResolver = context.getContentResolver();
        try (Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)) {
            if (cursor != null) {
                int dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                int count = 0;

                // Move cursor to the start index
                if (startIndex > 0) {
                    cursor.moveToPosition(startIndex);
                }

                while (cursor.moveToNext() && count < batchSize) {
                    String imagePath = cursor.getString(dataColumnIndex);
                    screenshotFilePaths.add(imagePath);
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return screenshotFilePaths;
    }
}
