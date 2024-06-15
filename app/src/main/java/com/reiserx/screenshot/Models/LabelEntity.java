package com.reiserx.screenshot.Models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "labels")
public class LabelEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String labelName;

    @Ignore
    public LabelEntity(String labelName) {
        this.labelName = labelName;
    }

    public LabelEntity(int id, String labelName) {
        this.id = id;
        this.labelName = labelName;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}