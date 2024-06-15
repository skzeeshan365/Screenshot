package com.reiserx.screenshot.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.reiserx.screenshot.Models.ImageEntity;

import java.util.List;

@Dao
public interface ImageDao {
    @Insert
    long insert(ImageEntity imageEntity);

    @Query("SELECT * FROM images")
    LiveData<List<ImageEntity>> getAllImages();

    @Query("SELECT * FROM images WHERE filePath = :filePath LIMIT 1")
    ImageEntity getImageByFilePath(String filePath);
}