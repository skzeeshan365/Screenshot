package com.reiserx.screenshot.Utils;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.reiserx.screenshot.DAO.ImageDao;
import com.reiserx.screenshot.DAO.LabelDao;
import com.reiserx.screenshot.DAO.LabeledScreenshotDao;
import com.reiserx.screenshot.Models.ImageEntity;
import com.reiserx.screenshot.Models.LabelEntity;
import com.reiserx.screenshot.Models.LabeledScreenshot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ImageEntity.class, LabelEntity.class, LabeledScreenshot.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    public abstract ImageDao imageDao();
    public abstract LabelDao labelDao();
    public abstract LabeledScreenshotDao labeledScreenshotDao();
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
