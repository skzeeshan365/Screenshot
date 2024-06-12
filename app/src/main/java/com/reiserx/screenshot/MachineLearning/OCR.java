package com.reiserx.screenshot.MachineLearning;

import android.content.Context;
import android.net.Uri;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.reiserx.screenshot.Interfaces.ScanCallback;

import java.io.IOException;

public class OCR {
    Context context;
    TextRecognizer recognizer;
    public static int ENGLISH = 0;
    public static int HINDI = 1;
    public static int KOREAN = 2;
    public static int JAPANESE = 3;
    public static int CHINESE = 4;

    public OCR(Context context) {
        this.context = context;
    }

    public void setRecognizer(int LangCode) {
        if (LangCode == ENGLISH)
            this.recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        else if (LangCode == HINDI)
            this.recognizer = TextRecognition.getClient(new DevanagariTextRecognizerOptions.Builder().build());
        else if (LangCode == KOREAN)
            recognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());
        else if (LangCode == JAPANESE)
            recognizer = TextRecognition.getClient(new JapaneseTextRecognizerOptions.Builder().build());
        else if (LangCode == CHINESE)
            recognizer = TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());

    }

    public InputImage prepareImage(Uri uri) {
        try {
            return InputImage.fromFilePath(context, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void scan(InputImage image, ScanCallback callback) {
        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    if (visionText.getText().trim().equals(""))
                        callback.onScanFailure("Failed");
                    else
                        callback.onScanSuccess(visionText.getText());
                })
                .addOnFailureListener(e -> {callback.onScanFailure(e.toString());
                });
    }
}
