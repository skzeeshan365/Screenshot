package com.reiserx.screenshot.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class SaveBitmap {
    Context context;
    Bitmap bitmap;

    public SaveBitmap(Bitmap bitmap, Context context) {
        this.bitmap = bitmap;
        this.context = context;
    }

    public void saveDataInApp() {
        // Specify the directory and filename
        File directory = context.getFilesDir();
        String filename = getRandom.getRandom(0, 1000000000) + ".png";
        File file = new File(directory, "Screenshots/"+filename);

        try {
            // Create necessary directories if they don't exist
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            // Save the bitmap to the file
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

            Toast.makeText(context, "Screenshot saved", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "An error occurred: " + e, Toast.LENGTH_LONG).show();
        }
    }

    public void saveDataLocalDCIM() {
        // Use MediaStore for Android 10 and above
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Screenshots");
        values.put(MediaStore.Images.Media.IS_PENDING, 1);

        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri != null) {
            try {
                OutputStream out = context.getContentResolver().openOutputStream(uri);
                if (out != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                }

                values.put(MediaStore.Images.Media.IS_PENDING, 0);
                context.getContentResolver().update(uri, values, null, null);

                Toast.makeText(context, "Screenshot saved in DCIM/Screenshots", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
    }
}
