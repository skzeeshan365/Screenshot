package com.reiserx.screenshot.Utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.gms.ads.MobileAds;
import com.reiserx.screenshot.Activities.MainActivity;
import com.reiserx.screenshot.Advertisements.InterstitialAds;

public class BaseApplication extends Application implements ViewModelStoreOwner {
    public static BaseApplication instance;
    RxDataStore<Preferences> dataStoreRX;

    String DATA_STORE = "screenshot_primary";

    private ViewModelStore viewModelStore;
    private boolean isMyActivityInForeground = false;
    private static AppDatabase database;
    static InterstitialAds interstitialAds;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        viewModelStore = new ViewModelStore();

        DataStore dataStoreSingleton = DataStore.getInstance();
        if (dataStoreSingleton.getDataStore() == null) {
            dataStoreRX = new RxPreferenceDataStoreBuilder(this, DATA_STORE).build();
        } else {
            dataStoreRX = dataStoreSingleton.getDataStore();
        }
        dataStoreSingleton.setDataStore(dataStoreRX);

        new Thread(
                () -> {
                    MobileAds.initialize(this);
                })
                .start();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
                // Check if the created activity is your specific activity
                if (activity instanceof MainActivity) {
                    isMyActivityInForeground = true;
                }
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                // Check if the resumed activity is your specific activity
                if (activity instanceof MainActivity) {
                    isMyActivityInForeground = true;
                }
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                // Check if the paused activity is your specific activity
                if (activity instanceof MainActivity) {
                    isMyActivityInForeground = false;
                }
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull  Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                // Check if the destroyed activity is your specific activity
                if (activity instanceof MainActivity) {
                    isMyActivityInForeground = false;
                }
            }
        });
        database = AppDatabase.getDatabase(this);
    }
    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
    public static BaseApplication getInstance() {
        return instance;
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return viewModelStore;
    }

    public boolean isMyActivityInForeground() {
        return isMyActivityInForeground;
    }

    public static AppDatabase getDatabase() {
        return database;
    }

    public static InterstitialAds getInterstitialAds() {
        return interstitialAds;
    }

    public static void setInterstitialAds(InterstitialAds interstitialAds) {
        BaseApplication.interstitialAds = interstitialAds;
    }
}
