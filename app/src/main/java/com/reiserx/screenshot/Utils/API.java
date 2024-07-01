package com.reiserx.screenshot.Utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.reiserx.screenshot.Interfaces.APICallback;
import com.reiserx.screenshot.Interfaces.RESTAPICallback;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class API {
    Context context;
    APICallback callback;

    public API(Context context, APICallback callback) {
        this.context = context;
        this.callback = callback;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null)
            auth.signInAnonymously().addOnFailureListener(e -> {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Alert");
                alert.setMessage("An error occurred: "+e.getMessage());
                alert.setPositiveButton("OK", null);
                alert.setCancelable(false);
                alert.show();
            });
    }

    public void getAPI(RESTAPICallback callback) throws GeneralSecurityException, IOException {
        Crypto crypto = new Crypto(context);
        if (crypto.getAPI_KEY() == null) {
            FirebaseDatabase.getInstance().getReference().child("API").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    com.reiserx.screenshot.Models.API api = snapshot.getValue(com.reiserx.screenshot.Models.API.class);
                    if (snapshot.exists()) {
                        crypto.setAPI_KEY(api.getAPI_KEY());
                        callback.onSuccess(api);
                    } else
                        callback.onSuccess(null);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onSuccess(null);
                }
            });
        } else {
            callback.onSuccess(new com.reiserx.screenshot.Models.API(crypto.getAPI_KEY()));
        }
    }

    public void explainImage(Uri uri, String API_KEY, String language) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        File file = getFileFromUri(uri, context);
        if (file != null) {
            RequestBody fileBody = RequestBody.create(file, MediaType.parse("application/octet-stream"));

            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", file.getName(), fileBody)
                    .addFormDataPart("text", "Please provide a detailed description of the image in "+language+" language, including the context, topic, and any identifiable objects or brands. Additionally, offer comprehensive information about the subjects and themes depicted within the image.")
                    .build();

            Request request = new Request.Builder()
                    .url("https://reiserx.com/ai/multimodel/")
                    .post(requestBody)
                    .addHeader("Authorization", API_KEY)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Gson gson = new GsonBuilder().create();
                        com.reiserx.screenshot.Models.Response data = gson.fromJson(responseData, com.reiserx.screenshot.Models.Response.class);
                        callback.onSuccess(data);
                    } else {
                        callback.onFailure(response.body().string());
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFailure(e.toString());
                }
            });
        } else {
            callback.onFailure("File not found");
        }
    }

    public static File getFileFromUri(Uri uri, Context context) {
        String filePath = null;

        // Check if the Uri scheme is "content"
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            filePath = getContentFilePath(uri, context);
        }
        // Check if the Uri scheme is "file"
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }

        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    private static String getContentFilePath(Uri uri, Context context) {
        Cursor cursor = null;
        final String[] projection = { MediaStore.Images.Media.DATA };

        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }
}
