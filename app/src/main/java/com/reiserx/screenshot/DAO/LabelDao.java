package com.reiserx.screenshot.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Embedded;
import androidx.room.Insert;
import androidx.room.Query;

import com.reiserx.screenshot.Models.LabelEntity;

import java.util.List;

@Dao
public interface LabelDao {
    @Insert
    long insert(LabelEntity labelEntity);

    @Query("SELECT * FROM labels")
    LiveData<List<LabelEntity>> getAllLabels();

    @Query("SELECT * FROM labels WHERE labelName = :labelName LIMIT 1")
    LabelEntity getLabelByName(String labelName);

    @Query("SELECT COUNT(*) FROM labels")
    LiveData<Integer> getLabelCount();

    @Query("SELECT labels.*, COUNT(image_label_cross_ref.imageId) AS imageCount " +
            "FROM labels " +
            "LEFT JOIN image_label_cross_ref ON labels.id = image_label_cross_ref.labelId " +
            "GROUP BY labels.id " +
            "ORDER BY imageCount DESC")
    LiveData<List<LabelWithImageCount>> getAllLabelsSortedByImageCount();

    // Define a POJO to hold LabelEntity and image count
    public static class LabelWithImageCount {
        @Embedded
        public LabelEntity label;

        @ColumnInfo(name = "imageCount")
        public int imageCount;

        // Constructor, getters, and setters if needed
        public LabelWithImageCount(LabelEntity label, int imageCount) {
            this.label = label;
            this.imageCount = imageCount;
        }
    }
}