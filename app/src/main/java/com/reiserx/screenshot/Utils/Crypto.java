package com.reiserx.screenshot.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Crypto {
    Context context;
    String API_KEY = "API_KEY";
    SharedPreferences sharedPreferences;

    public Crypto(Context context) throws GeneralSecurityException, IOException {
        this.context = context;
        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        sharedPreferences = EncryptedSharedPreferences.create(
                context,
                "secret_shared_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    public String getAPI_KEY() {
        return sharedPreferences.getString(API_KEY, null);
    }

    public void setAPI_KEY(String API) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(API_KEY, API);
        editor.apply();
    }
}
