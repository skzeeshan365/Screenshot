package com.reiserx.screenshot.MachineLearning;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.reiserx.screenshot.Interfaces.ImageLabelCallBack;

import java.util.ArrayList;
import java.util.List;

public class ImageLabelling extends MachineLearning {
    ImageLabeler labeler;
    ImageLabelCallBack callBack;

    public ImageLabelling(ImageLabelCallBack callBack) {
        labeler = ImageLabeling.getClient(new ImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.7f)
                .build()
        );
        this.callBack = callBack;
    }

    public void label(InputImage image) {
        List<String> labels = new ArrayList<>();
        labeler.process(image)
                .addOnSuccessListener(imageLabels -> {
                    for (ImageLabel label : imageLabels) {
                        labels.add(label.getText());
                    }
                    callBack.onSuccess(labels);
                })
                .addOnFailureListener(e -> {
                    callBack.onFailure(e.toString());
                });

    }
}
