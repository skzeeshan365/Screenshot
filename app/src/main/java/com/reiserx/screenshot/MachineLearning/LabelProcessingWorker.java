package com.reiserx.screenshot.MachineLearning;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.mlkit.vision.common.InputImage;
import com.reiserx.screenshot.Interfaces.ImageLabelCallBack;
import com.reiserx.screenshot.Repositories.LabelsRepository;

import java.io.File;
import java.util.List;

public class LabelProcessingWorker extends Worker {
    private final ImageProcessing imageProcessing;

    public LabelProcessingWorker (@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        imageProcessing = new ImageProcessing(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        List<String> screenshotFilePaths = imageProcessing.getScreenshotsInBatches(1000);

        if (screenshotFilePaths != null && !screenshotFilePaths.isEmpty()) {
            for (String filePath : screenshotFilePaths) {
                // Prepare and label the image
                    ImageLabelling imageLabelling = new ImageLabelling(new ImageLabelCallBack() {
                        @Override
                        public void onSuccess(List<String> labels) {
                            Log.d("dfdsfs", "log");
                            LabelsRepository labelsRepository = new LabelsRepository();
                            labelsRepository.insertLabeledScreenshot(filePath, labels);
                        }

                        @Override
                        public void onFailure(String e) {

                        }
                    });
                    InputImage inputImage = imageLabelling.prepareImage(getApplicationContext(), Uri.fromFile(new File(filePath)));
                    if (inputImage != null)
                        imageLabelling.label(inputImage);
            }
            return Result.success();
        } else {
            return Result.retry();
        }
    }
}