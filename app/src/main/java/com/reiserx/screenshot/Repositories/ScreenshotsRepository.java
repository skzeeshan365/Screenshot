package com.reiserx.screenshot.Repositories;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import com.google.android.gms.ads.nativead.NativeAd;
import com.reiserx.screenshot.Activities.ui.settings.FileFragment;
import com.reiserx.screenshot.Advertisements.NativeAds;
import com.reiserx.screenshot.Models.ScreenshotLabels;
import com.reiserx.screenshot.Models.Screenshots;
import com.reiserx.screenshot.Utils.DataStoreHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScreenshotsRepository {
    private final OnGetScreenshotsComplete onGetScreenshotsComplete;
    private final OnGetSilentScreenshotsComplete onGetSilentScreenshotsComplete;
    private final OnGetFilesComplete onGetFilesComplete;
    private final OnGetLabelsComplete onGetLabelsComplete;

    DataStoreHelper dataStoreHelper;
    String filepath_parent = null;

    public ScreenshotsRepository(OnGetScreenshotsComplete onGetScreenshotsComplete,
                                 OnGetSilentScreenshotsComplete onGetSilentScreenshotsComplete,
                                 OnGetFilesComplete onGetFilesComplete,
                                 OnGetLabelsComplete onGetLabelsComplete) {
        this.onGetScreenshotsComplete = onGetScreenshotsComplete;
        this.onGetSilentScreenshotsComplete = onGetSilentScreenshotsComplete;
        this.onGetFilesComplete = onGetFilesComplete;
        this.onGetLabelsComplete = onGetLabelsComplete;
        dataStoreHelper = new DataStoreHelper();
    }

    public void getScreenshots(Context context, String label) {
        List<Screenshots> imagePaths = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Images.Media.DATA
        };

        String selection = MediaStore.Images.Media.DATA + " like ?";
        String[] selectionArgs = new String[]{"%/DCIM/" + dataStoreHelper.getStringValue(FileFragment.DEFAULT_STORAGE_KEY, null) + "/%"};

        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        ContentResolver contentResolver = context.getContentResolver();
        try (Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)) {
            if (cursor != null) {
                int dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (label == null || Objects.equals(label, "null")) {
                    while (cursor.moveToNext()) {
                        String imagePath = cursor.getString(dataColumnIndex);
                        imagePaths.add(new Screenshots(imagePath));
                    }
                } else {
                    while (cursor.moveToNext()) {
                        String imagePath = cursor.getString(dataColumnIndex);
                        String label2 = extractAppLabelFromFilename(imagePath);
                        if (label.equals(label2)) {
                            imagePaths.add(new Screenshots(imagePath));
                        }
                    }
                }

                if (imagePaths.isEmpty())
                    onGetScreenshotsComplete.onFailure("No available screenshot");
                else {
                    int numberOfAds = imagePaths.size() / 3;
                    onGetScreenshotsComplete.onSuccess(imagePaths);
                    new Thread(() -> {
                        NativeAds nativeAds = new NativeAds(context);
                        nativeAds.prefetchAds(numberOfAds, () -> {
                            onGetScreenshotsComplete.onSuccessAds(nativeAds.getAdList());
                        });
                    }).start();
                }
            } else {
                onGetScreenshotsComplete.onFailure("Failed to get screenshots");
            }
        } catch (Exception e) {
            onGetScreenshotsComplete.onFailure("Exception: " + e.getMessage());
            throw e;
        }
    }

    public void getScreenshotLabels(Context context) {
        filepath_parent = null;
        List<ScreenshotLabels> labels = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Images.Media.DATA
        };

        String selection = MediaStore.Images.Media.DATA + " like ?";
        String[] selectionArgs = new String[]{"%/DCIM/" + dataStoreHelper.getStringValue(FileFragment.DEFAULT_STORAGE_KEY, null) + "/%"};

        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        ContentResolver contentResolver = context.getContentResolver();
        try (Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)) {
            if (cursor != null) {
                int dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                while (cursor.moveToNext()) {
                    String imagePath = cursor.getString(dataColumnIndex);
                    String label = extractAppLabelFromFilename(imagePath);
                    if (label != null && !containsLabel(labels, label)) {
                        labels.add(new ScreenshotLabels(label, imagePath));
                    }
                    if (filepath_parent == null)
                        filepath_parent = imagePath;
                }

                if (labels.isEmpty())
                    onGetLabelsComplete.onLabelsFailure("No available screenshot");
                else {
                    labels.add(0, new ScreenshotLabels("All screenshots", filepath_parent));
                    insertAdElementsAtRandomPositions(labels, context);
                }
            } else {
                onGetLabelsComplete.onLabelsFailure("Failed to get screenshots");
            }
        } catch (Exception e) {
            onGetLabelsComplete.onLabelsFailure("Exception: " + e.getMessage());
        }
    }

    private void insertAdElementsAtRandomPositions(List<ScreenshotLabels> labels, Context context) {
        int numberOfAds = labels.size() / 3; // Number of ad elements based on the list size
        onGetLabelsComplete.onLabelsSuccess(labels);
        new Thread(() -> {
            NativeAds nativeAds = new NativeAds(context);
            nativeAds.prefetchAds(numberOfAds, () -> {
                new Handler(Looper.getMainLooper()).post(() -> {
                    onGetLabelsComplete.onLabelsLoadedAds(nativeAds.getAdList());
                });
            });
        }).start();
    }

    private boolean containsLabel(List<ScreenshotLabels> labels, String label) {
        for (ScreenshotLabels screenshotLabels : labels) {
            if (screenshotLabels.getLabel().equals(label)) {
                return true;
            }
        }
        return false;
    }


    private String extractAppLabelFromFilename(String filename) {
        String[] parts = filename.split("_");
        for (int i = parts.length - 1; i >= 0; i--) {
            String part = parts[i];
            if (!part.isEmpty()) {
                // Remove the file extension (e.g., ".png")
                int extensionIndex = part.lastIndexOf('.');
                if (extensionIndex != -1) {
                    return part.substring(0, extensionIndex);
                } else {
                    return part; // Return the part as is if there is no extension
                }
            }
        }
        return null; // No valid app label found
    }


    public void getScreenshotsInApp(Context context) {
        List<Screenshots> imagePaths = new ArrayList<>();

        // Specify the directory for the Screenshots in your app's internal storage
        File directory = new File(context.getFilesDir(), "Screenshots");

        // List files directly from your app's directory
        File[] files = directory.listFiles();

        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isFile()) {
                    String imagePath = file.getAbsolutePath();
                    imagePaths.add(new Screenshots(imagePath));
                }
            }

            if (imagePaths.isEmpty())
                onGetSilentScreenshotsComplete.onSilentFailure("No available screenshots");
            else {
                int numberOfAds = imagePaths.size() / 3;
                onGetSilentScreenshotsComplete.onSilentSuccess(imagePaths);
                new Thread(() -> {
                    NativeAds nativeAds = new NativeAds(context);
                    nativeAds.prefetchAds(numberOfAds, () -> {
                        onGetSilentScreenshotsComplete.onSilentSuccessAds(nativeAds.getAdList());
                    });
                }).start();
            }
        } else {
            onGetSilentScreenshotsComplete.onSilentFailure("No available screenshots");
        }
    }

    public void getDCIMDirectory() {
        List<String> dcimDirectories = new ArrayList<>();

        // Get the public DCIM directory
        File dcimDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        // Check if the directory exists and is a directory
        if (dcimDirectory.exists() && dcimDirectory.isDirectory()) {
            // List subdirectories
            File[] subdirectories = dcimDirectory.listFiles();

            if (subdirectories != null) {
                for (File subdirectory : subdirectories) {
                    // Check if it is a directory
                    if (subdirectory.isDirectory()) {
                        // Add the directory name to the list
                        dcimDirectories.add(subdirectory.getName());
                    }
                }
            }
        }

        // Now dcimDirectories contains public DCIM directories
        // Handle the list as needed
        onGetFilesComplete.onFilesSuccess(dcimDirectories);
    }


    public interface OnGetScreenshotsComplete {
        void onSuccess(List<Screenshots> parentItemList);

        void onSuccessAds(List<NativeAd> nativeAdList);

        void onFailure(String error);
    }

    public interface OnGetSilentScreenshotsComplete {
        void onSilentSuccess(List<Screenshots> parentItemList);

        void onSilentSuccessAds(List<NativeAd> parentItemList);

        void onSilentFailure(String error);
    }

    public interface OnGetFilesComplete {
        void onFilesSuccess(List<String> fileList);

        void onFilesFailure(String error);
    }

    public interface OnGetLabelsComplete {
        void onLabelsSuccess(List<ScreenshotLabels> labelsList);

        void onLabelsLoadedAds(List<NativeAd> nativeAdList);

        void onLabelsFailure(String error);
    }
}
