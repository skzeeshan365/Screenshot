package com.reiserx.screenshot.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.reiserx.screenshot.Advertisements.BannerAds;
import com.reiserx.screenshot.Interfaces.APICallback;
import com.reiserx.screenshot.Models.Response;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.API;
import com.reiserx.screenshot.databinding.ActivityAiactivityBinding;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;

public class AIActivity extends AppCompatActivity implements APICallback {

    ActivityAiactivityBinding binding;
    Uri uri;
    boolean temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAiactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tv.setTextColor(getColor(R.color.button_design_text));

        binding.progressBar4.setVisibility(View.VISIBLE);
        binding.scrollView2.setVisibility(View.GONE);

        BannerAds bannerAds = new BannerAds(this, binding.adView);
        bannerAds.loadBanner();

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            uri = intent.getData();
            temp = intent.getBooleanExtra("temp", false);

            binding.img.setImageURI(uri);
            API api = new API(this, this);
            try {
                api.getAPI(api1 -> {
                    if (api1 != null)
                        api.explainImage(uri, api1.getAPI_KEY());
                    else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(this);
                        alert.setTitle("Alert");
                        alert.setMessage("An error occurred, please try again later");
                        alert.setPositiveButton("OK", (dialog, which) -> finish());
                        alert.setCancelable(false);
                        alert.show();
                    }
                });
            } catch (GeneralSecurityException | IOException e) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Alert");
                alert.setMessage("An error occurred: "+e.toString());
                alert.setPositiveButton("OK", (dialog, which) -> finish());
                alert.setCancelable(false);
                alert.show();
            }
        }
    }

    @Override
    public void onSuccess(Response response) {
        String result = response.getResponse().replace("\\n\\n", "\n\n");
        Markwon markwon = Markwon.builder(this)
                .usePlugin(TaskListPlugin.create(this))
                .usePlugin(HtmlPlugin.create())
                .usePlugin(LinkifyPlugin.create())
                .build();

        // Set HTML to TextView
        runOnUiThread(() -> {
            markwon.setMarkdown(binding.tv, result);
            binding.progressBar4.setVisibility(View.GONE);
            binding.scrollView2.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onFailure(String message) {
        runOnUiThread(() -> {
            binding.tv.setText(message);
            binding.progressBar4.setVisibility(View.GONE);
            binding.scrollView2.setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (temp) {
            File file = API.getFileFromUri(uri, this);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}