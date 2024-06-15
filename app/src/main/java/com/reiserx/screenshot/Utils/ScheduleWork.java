package com.reiserx.screenshot.Utils;

import android.content.Context;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.reiserx.screenshot.MachineLearning.LabelProcessingWorker;

import java.util.concurrent.TimeUnit;

public class ScheduleWork {
    public static void scheduleLabelProcessing(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        workManager.getWorkInfosByTagLiveData("label_processing").observeForever(workInfos -> {
            boolean isWorkScheduled = false;

            if (workInfos != null && !workInfos.isEmpty()) {
                for (WorkInfo workInfo : workInfos) {
                    if (workInfo.getState() == WorkInfo.State.ENQUEUED ||
                            workInfo.getState() == WorkInfo.State.RUNNING) {
                        isWorkScheduled = true;
                        break;
                    }
                }
            }

            if (!isWorkScheduled) {
                PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
                        LabelProcessingWorker.class,
                        1,
                        TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                        .addTag("label_processing")
                        .build();

                workManager.enqueue(periodicWorkRequest);
            }
        });
    }
}
