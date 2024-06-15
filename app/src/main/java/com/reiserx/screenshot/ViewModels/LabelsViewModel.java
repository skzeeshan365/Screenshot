package com.reiserx.screenshot.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.reiserx.screenshot.DAO.LabelDao;
import com.reiserx.screenshot.Models.ImageEntity;
import com.reiserx.screenshot.Models.LabelEntity;
import com.reiserx.screenshot.Repositories.LabelsRepository;

import java.util.List;

public class LabelsViewModel extends AndroidViewModel {
    private LabelsRepository repository;
    private LiveData<List<ImageEntity>> allImages;
    private LiveData<List<LabelDao.LabelWithImageCount>> allLabels;
    private LiveData<Integer> labelCount;

    public LabelsViewModel(@NonNull Application application) {
        super(application);
        repository = new LabelsRepository();
    }

    public LiveData<List<LabelDao.LabelWithImageCount>> getAllLabels() {
        if (allLabels == null) {
            allLabels = repository.getLabels();
        }
        return allLabels;
    }

    public LiveData<Integer> getLabelCount() {
        if (labelCount == null) {
            labelCount = repository.getLabelCount();
        }
        return labelCount;
    }

    public LiveData<List<ImageEntity>> getImagesByLabel(int labelId) {
        if (allImages == null) {
            allImages = repository.getImagesByLabel(labelId);
        }
        return allImages;
    }

    public void insertImage(ImageEntity imageEntity) {
        repository.insertImage(imageEntity);
    }

    public void insertLabel(LabelEntity labelEntity) {
        repository.insertLabel(labelEntity);
    }

    public void insertLabeledScreenshot(String path, List<String> labels) {
        repository.insertLabeledScreenshot(path, labels);
    }

    public LiveData<List<LabelEntity>> getLabelsForImage(int imageId) {
        return repository.getLabelsForImage(imageId);
    }
}