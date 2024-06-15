package com.reiserx.screenshot.Models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "image_label_cross_ref",
        primaryKeys = {"imageId", "labelId"},
        foreignKeys = {
                @ForeignKey(entity = ImageEntity.class,
                        parentColumns = "id",
                        childColumns = "imageId"),
                @ForeignKey(entity = LabelEntity.class,
                        parentColumns = "id",
                        childColumns = "labelId"),
        },
                indices = {@Index("labelId")
        })
public class LabeledScreenshot {
    public int imageId;
    public int labelId;

    public LabeledScreenshot(int imageId, int labelId) {
        this.imageId = imageId;
        this.labelId = labelId;
    }
}
