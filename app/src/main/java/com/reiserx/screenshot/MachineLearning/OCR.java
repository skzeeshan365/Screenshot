package com.reiserx.screenshot.MachineLearning;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.reiserx.screenshot.Activities.OCRActivity;

import java.io.IOException;

public class OCR {
    Context context;
    TextRecognizer recognizer;
    String TAG = "dfdsfs";

    public OCR(Context context) {
        this.context = context;
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    public InputImage prepareImage(Uri uri) {
        try {
            return InputImage.fromFilePath(context, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void scan(InputImage image) {
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(visionText -> {
                            Log.d(TAG, String.valueOf(visionText.getText()));
                            Intent intent = new Intent(context, OCRActivity.class);
                            intent.putExtra("message", visionText.getText());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        })
                        .addOnFailureListener(
                                e -> {
                                    Log.e(TAG, e.toString());
                                });
    }
}
