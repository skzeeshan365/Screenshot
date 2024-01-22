package com.reiserx.screenshot.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava2.RxDataStore;

import java.util.Map;

import io.reactivex.Single;

public class DataStoreHelper {
    RxDataStore<Preferences> dataStoreRX;

    Preferences pref_error = new Preferences() {
        @Nullable
        @Override
        public <T> T get(@NonNull Key<T> key) {
            return null;
        }

        @Override
        public <T> boolean contains(@NonNull Key<T> key) {
            return false;
        }

        @NonNull
        @Override
        public Map<Key<?>, Object> asMap() {
            return null;
        }
    };

    public DataStoreHelper() {
        this.dataStoreRX = DataStore.getInstance().getDataStore();
    }

    public boolean putStringValue(String Key, String value){
        Preferences.Key<String>PREF_KEY = PreferencesKeys.stringKey(Key);
        boolean returnvalue;
        Single<Preferences> updateResult =  dataStoreRX.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(PREF_KEY, value);
            return Single.just(mutablePreferences);
        }).onErrorReturnItem(pref_error);
        returnvalue = updateResult.blockingGet() != pref_error;
        return returnvalue;
    }
    public String getStringValue(String Key, String def) {
        Preferences.Key<String> PREF_KEY = PreferencesKeys.stringKey(Key);
            Single<String> value = dataStoreRX.data().firstOrError().map(prefs -> prefs.get(PREF_KEY)).onErrorReturnItem(String.valueOf(def));
            return value.blockingGet();
    }

    public boolean putBooleanValue(String Key, boolean value){
        Preferences.Key<Boolean> PREF_KEY = PreferencesKeys.booleanKey(Key);
        boolean returnvalue;
        Single<Preferences> updateResult =  dataStoreRX.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(PREF_KEY, value);
            return Single.just(mutablePreferences);
        }).onErrorReturnItem(pref_error);
        returnvalue = updateResult.blockingGet() != pref_error;
        return returnvalue;
    }

    public boolean getBooleanValue(String key, boolean def) {
        Preferences.Key<Boolean> PREF_KEY = PreferencesKeys.booleanKey(key);
        Single<Boolean> value = dataStoreRX.data().firstOrError().map(prefs -> prefs.get(PREF_KEY)).onErrorReturnItem(def);
        return value.blockingGet();
    }

    public boolean putIntValue(String Key, int value){
        Preferences.Key<Integer> PREF_KEY = PreferencesKeys.intKey(Key);
        boolean returnvalue;
        Single<Preferences> updateResult =  dataStoreRX.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(PREF_KEY, value);
            return Single.just(mutablePreferences);
        }).onErrorReturnItem(pref_error);
        returnvalue = updateResult.blockingGet() != pref_error;
        return returnvalue;
    }

    public int getIntValue(String key, int def) {
        Preferences.Key<Integer> PREF_KEY = PreferencesKeys.intKey(key);
        Single<Integer> value = dataStoreRX.data().firstOrError().map(prefs -> prefs.get(PREF_KEY)).onErrorReturnItem(def);
        return value.blockingGet();
    }

    public void removeValue(String key) {
        Preferences.Key<String> PREF_KEY = PreferencesKeys.stringKey(key);
        dataStoreRX.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.remove(PREF_KEY);
            return Single.just(mutablePreferences);
        }).subscribe();
    }
}
