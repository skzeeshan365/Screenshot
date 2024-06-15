package com.reiserx.screenshot.Repositories;

import androidx.lifecycle.LiveData;

import com.reiserx.screenshot.DAO.ImageDao;
import com.reiserx.screenshot.DAO.LabelDao;
import com.reiserx.screenshot.DAO.LabeledScreenshotDao;
import com.reiserx.screenshot.Models.ImageEntity;
import com.reiserx.screenshot.Models.LabelEntity;
import com.reiserx.screenshot.Models.LabeledScreenshot;
import com.reiserx.screenshot.Utils.AppDatabase;
import com.reiserx.screenshot.Utils.BaseApplication;

import java.util.List;

public class LabelsRepository {
    private ImageDao imageDao;
    private LabelDao labelDao;
    private LabeledScreenshotDao labelScreenshotDao;
    public LabelsRepository() {
        AppDatabase db = BaseApplication.getDatabase();
        imageDao = db.imageDao();
        labelDao = db.labelDao();
        labelScreenshotDao = db.labeledScreenshotDao();
    }

    public LiveData<List<ImageEntity>> getAllImages() {
        return imageDao.getAllImages();
    }

    public LiveData<List<LabelDao.LabelWithImageCount>> getLabels() {
        return labelDao.getAllLabelsSortedByImageCount();
    }

    public void insertImage(ImageEntity imageEntity) {
            imageDao.insert(imageEntity);
    }

    public void insertLabel(LabelEntity labelEntity) {
            labelDao.insert(labelEntity);
    }

    public LiveData<Integer> getLabelCount() {
        return labelDao.getLabelCount();
    }

    public LiveData<List<ImageEntity>> getImagesByLabel(int labelId) {
        return labelScreenshotDao.getImagesByLabel(labelId);
    }

    public void insertLabeledScreenshot(String imageFilePath, List<String> labelNames) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Insert image if not already present
            ImageEntity imageEntity = imageDao.getImageByFilePath(imageFilePath);
            if (imageEntity == null) {
                long imageId = imageDao.insert(new ImageEntity(imageFilePath));
                imageEntity = new ImageEntity(imageFilePath);
                imageEntity.setId((int) imageId);
            }

            // Insert labels and cross-reference
            for (String labelName : labelNames) {
                LabelEntity labelEntity = labelDao.getLabelByName(labelName);
                if (labelEntity == null) {
                    long labelId = labelDao.insert(new LabelEntity(labelName));
                    labelEntity = new LabelEntity(labelName);
                    labelEntity.setId((int) labelId);
                }

                // Insert cross-reference if not already exists
                LabeledScreenshot existingLabeledScreenshot = labelScreenshotDao.getByImageIdAndLabelId(imageEntity.getId(), labelEntity.getId());
                if (existingLabeledScreenshot == null) {
                    labelScreenshotDao.insert(new LabeledScreenshot(imageEntity.getId(), labelEntity.getId()));
                }
            }
        });
    }

    public LiveData<List<LabelEntity>> getLabelsForImage(int imageId) {
        return labelScreenshotDao.getLabelsForImage(imageId);
    }
}
