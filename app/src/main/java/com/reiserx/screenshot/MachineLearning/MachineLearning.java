package com.reiserx.screenshot.MachineLearning;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;

public class MachineLearning {
    public InputImage prepareImage(Context context, Uri uri) {
        try {
            return InputImage.fromFilePath(context, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public InputImage prepareImage(Bitmap bitmap) {
        return InputImage.fromBitmap(bitmap, 0);
    }
}
