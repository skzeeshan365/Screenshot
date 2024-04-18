package com.reiserx.screenshot.ViewModels;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.reiserx.screenshot.Models.Screenshots;
import com.reiserx.screenshot.Repositories.ScreenshotsRepository;

import java.util.List;

public class ScreenshotsViewModel extends ViewModel implements
        ScreenshotsRepository.OnGetScreenshotsComplete,
        ScreenshotsRepository.OnGetSilentScreenshotsComplete,
        ScreenshotsRepository.OnGetFilesComplete {
    private final ScreenshotsRepository screenshotsRepository;

    private final MutableLiveData<List<Screenshots>> ItemMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Screenshots>> ItemSilentMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> ItemFilesMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> ErrorMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> ErrorSilentMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> ErrorFilesMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Screenshots>> getItemMutableLiveData() {
        return ItemMutableLiveData;
    }
    public MutableLiveData<List<Screenshots>> getSilentItemMutableLiveData() {
        return ItemSilentMutableLiveData;
    }

    public MutableLiveData<List<String>> getFilesMutableLiveData() {
        return ItemFilesMutableLiveData;
    }

    public MutableLiveData<String> getErrorMutableLiveData() {
        return ErrorMutableLiveData;
    }
    public MutableLiveData<String> getErrorSilentMutableLiveData() {
        return ErrorSilentMutableLiveData;
    }

    public MutableLiveData<String> getErrorFilesMutableLiveData() {
        return ErrorFilesMutableLiveData;
    }

    public ScreenshotsViewModel() {
        screenshotsRepository = new ScreenshotsRepository(this, this, this);
    }

    public void getScreenshots(Context context) {
        new Thread(() -> {
            screenshotsRepository.getScreenshots(context);
        }).start();
    }

    public void getScreenshotsInApp(Context context) {
        screenshotsRepository.getScreenshotsInApp(context);
    }

    public void getDCIMDirectory() {
        screenshotsRepository.getDCIMDirectory();
    }

    @Override
    public void onSuccess(List<Screenshots> parentItemList) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
        ItemMutableLiveData.setValue(parentItemList);
        });
    }

    @Override
    public void onFailure(String error) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
        ErrorMutableLiveData.setValue(error);
        });
    }

    @Override
    public void onSilentSuccess(List<Screenshots> parentItemList) {
        ItemSilentMutableLiveData.setValue(parentItemList);
    }

    @Override
    public void onSilentFailure(String error) {
        ErrorSilentMutableLiveData.setValue(error);
    }

    @Override
    public void onFilesSuccess(List<String> fileList) {
        ItemFilesMutableLiveData.setValue(fileList);
    }

    @Override
    public void onFilesFailure(String error) {
        ErrorFilesMutableLiveData.setValue(error);
    }
}