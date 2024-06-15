package com.reiserx.screenshot.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.reiserx.screenshot.Models.ImageEntity;
import com.reiserx.screenshot.Models.LabelEntity;
import com.reiserx.screenshot.Models.LabeledScreenshot;

import java.util.List;

@Dao
public interface LabeledScreenshotDao {
    @Insert
    void insert(LabeledScreenshot imageLabelCrossRef);

    @Query("SELECT labels.* FROM labels " +
            "INNER JOIN image_label_cross_ref ON labels.id = image_label_cross_ref.labelId " +
            "WHERE image_label_cross_ref.imageId = :imageId")
    LiveData<List<LabelEntity>> getLabelsForImage(final int imageId);

    @Query("SELECT images.* FROM images " +
            "INNER JOIN image_label_cross_ref ON images.id = image_label_cross_ref.imageId " +
            "WHERE image_label_cross_ref.labelId = :labelId")
    LiveData<List<ImageEntity>> getImagesByLabel(final int labelId);

    @Query("SELECT * FROM image_label_cross_ref " +
            "WHERE imageId = :imageId AND labelId = :labelId LIMIT 1")
    LabeledScreenshot getByImageIdAndLabelId(int imageId, int labelId);
}