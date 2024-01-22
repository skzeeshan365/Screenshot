package com.reiserx.screenshot.ViewModels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.reiserx.screenshot.Models.Screenshots;
import com.reiserx.screenshot.Repositories.ScreenshotsRepository;

import java.util.List;

public class ScreenshotsViewModel extends ViewModel implements ScreenshotsRepository.OnGetScreenshotsComplete, ScreenshotsRepository.OnGetSilentScreenshotsComplete {
    private final ScreenshotsRepository screenshotsRepository;

    private final MutableLiveData<List<Screenshots>> ItemMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Screenshots>> ItemSilentMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> ErrorMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> ErrorSilentMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Screenshots>> getItemMutableLiveData() {
        return ItemMutableLiveData;
    }
    public MutableLiveData<List<Screenshots>> getSilentItemMutableLiveData() {
        return ItemSilentMutableLiveData;
    }

    public MutableLiveData<String> getErrorMutableLiveData() {
        return ErrorMutableLiveData;
    }
    public MutableLiveData<String> getErrorSilentMutableLiveData() {
        return ErrorSilentMutableLiveData;
    }

    public ScreenshotsViewModel() {
        screenshotsRepository = new ScreenshotsRepository(this, this);
    }

    public void getScreenshots(Context context, int limit) {
        screenshotsRepository.getScreenshots(context, limit);
    }

    public void getScreenshotsInApp(Context context) {
        screenshotsRepository.getScreenshotsInApp(context);
    }

    @Override
    public void onSuccess(List<Screenshots> parentItemList) {
        ItemMutableLiveData.setValue(parentItemList);
    }

    @Override
    public void onFailure(String error) {
        ErrorMutableLiveData.setValue(error);
    }

    @Override
    public void onSilentSuccess(List<Screenshots> parentItemList) {
        ItemSilentMutableLiveData.setValue(parentItemList);
    }

    @Override
    public void onSilentFailure(String error) {
        ErrorSilentMutableLiveData.setValue(error);
    }
}