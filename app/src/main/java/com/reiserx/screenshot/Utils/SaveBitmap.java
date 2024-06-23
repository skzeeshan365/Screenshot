package com.reiserx.screenshot.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.exifinterface.media.ExifInterface;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.reiserx.screenshot.Activities.ui.home.GalleryFragment;
import com.reiserx.screenshot.Activities.ui.settings.FileFragment;
import com.reiserx.screenshot.Interfaces.FusedLocationResult;
import com.reiserx.screenshot.Interfaces.SaveBitmapInterface;
import com.reiserx.screenshot.ViewModels.ScreenshotsViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class SaveBitmap {
    Context context;
    Bitmap bitmap;
    DataStoreHelper dataStoreHelper;
    ScreenshotsViewModel viewModel;
    Location location;

    public SaveBitmap(Bitmap bitmap, Context context, SaveBitmapInterface saveBitmapInterface) {
        this.bitmap = bitmap;
        this.context = context;
        dataStoreHelper = new DataStoreHelper();
        viewModel = new ViewModelProvider((ViewModelStoreOwner) context.getApplicationContext()).get(ScreenshotsViewModel.class);
        FusedLocation fusedLocation = new FusedLocation(context, new FusedLocationResult() {
            @Override
            public void onSuccess(Location loc) {
                location = loc;
                saveBitmapInterface.onSuccess(SaveBitmap.this);
            }

            @Override
            public void onFailure(Exception e) {
                location = null;
                saveBitmapInterface.onSuccess(SaveBitmap.this);
            }
        });
        fusedLocation.getLocation();
    }

    public File saveDataInApp() {
        // Specify the directory and filename
        File directory = context.getFilesDir();
        String filename = getRandom.getRandom(0, 1000000000) + ".png";
        File file = new File(directory, "Screenshots/"+filename);

        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            attachLocationMetadata(file.getAbsolutePath());

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

            if (BaseApplication.getInstance().isMyActivityInForeground())
                viewModel.getScreenshotsInApp(context);
            return file;
        } catch (Exception e) {
            Toast.makeText(context, "An error occurred: " + e, Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public File saveDataLocalDCIM(String package_name) {
        // Use MediaStore for Android 10 and above
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/"+dataStoreHelper.getStringValue(FileFragment.DEFAULT_STORAGE_KEY, null));
        values.put(MediaStore.Images.Media.IS_PENDING, 1);

        if (package_name != null) {
            String filename = generateFilename(package_name);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        }

        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri != null) {
            try {
                OutputStream out = context.getContentResolver().openOutputStream(uri);
                if (out != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                }

                String path = getFileFromUri(uri, context).getAbsolutePath();
                attachLocationMetadata(path);

                values.put(MediaStore.Images.Media.IS_PENDING, 0);
                context.getContentResolver().update(uri, values, null, null);

                if (BaseApplication.getInstance().isMyActivityInForeground()) {
                    viewModel.getScreenshots(context, dataStoreHelper.getStringValue(GalleryFragment.SCREENSHOT_LABEL, null));
                }
                return getFileFromUri(uri, context);
            } catch (Exception e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
        return null;
    }

    public static File getFileFromUri(Uri uri, Context context) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            return new File(filePath);
        }
        return null;
    }

    private String generateFilename(String package_name) {
        long timestamp = System.currentTimeMillis();
        return "Screenshot_" + timestamp + "_" + package_name + ".png";
    }

    public static void deleteScreenshotDCIM(Context context, ArrayList<Uri> arrayList, ActivityResultLauncher<IntentSenderRequest> deleteResultLauncher) {
        ContentResolver contentResolver = context.getContentResolver();
        ArrayList<Uri> contentUris = new ArrayList<>();

        for (Uri fileUri : arrayList) {
            // Query the MediaStore to get the content URI for the given file URI
            String[] projection = {MediaStore.Images.Media._ID};
            String selection = MediaStore.Images.Media.DATA + "=?";
            String[] selectionArgs = {new File(fileUri.getPath()).getAbsolutePath()};
            Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                contentUris.add(contentUri);
                cursor.close();
            } else {
                // Handle the case where the content URI could not be retrieved
                Log.e("DeleteScreenshotDCIM", "Failed to retrieve content URI for: " + fileUri.toString());
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        try {
            // Use the obtained content URIs for deletion
            IntentSender intentSender = MediaStore.createDeleteRequest(contentResolver, contentUris).getIntentSender();
            IntentSenderRequest senderRequest = new IntentSenderRequest.Builder(intentSender)
                    .setFillInIntent(null)
                    .setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION, 0)
                    .build();
            deleteResultLauncher.launch(senderRequest);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            // Handle the IllegalArgumentException
            Log.e("DeleteScreenshotDCIM", "IllegalArgumentException: " + e.getMessage());
        }
    }

    public static File createDirectoryInDCIM(String folderName) {
        File dcimDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), folderName);

        // Check if the directory already exists
        if (!dcimDirectory.exists()) {
            // Create the directory if it doesn't exist
            if (dcimDirectory.mkdirs()) {
                return dcimDirectory;
            } else {
                // Failed to create the directory
                return null;
            }
        } else {
            // Directory already exists
            return dcimDirectory;
        }
    }

    private void attachLocationMetadata(String imagePath) {
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            if (location != null) {
                exif.setGpsInfo(location);
                exif.saveAttributes();
            }
        } catch (IOException e) {
            Toast.makeText(context, "Error attaching location metadata: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
