package com.reiserx.screenshot.Utils;

import android.app.Application;
import android.content.Context;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;

public class BaseApplication extends Application {
    public static BaseApplication instance;
    RxDataStore<Preferences> dataStoreRX;

    String DATA_STORE = "screenshot_primary";
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        DataStore dataStoreSingleton = DataStore.getInstance();
        if (dataStoreSingleton.getDataStore() == null) {
            dataStoreRX = new RxPreferenceDataStoreBuilder(this, DATA_STORE).build();
        } else {
            dataStoreRX = dataStoreSingleton.getDataStore();
        }
        dataStoreSingleton.setDataStore(dataStoreRX);
    }
    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
    public static BaseApplication getInstance() {
        return instance;
    }
}
