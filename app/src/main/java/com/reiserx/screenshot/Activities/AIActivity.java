package com.reiserx.screenshot.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.reiserx.screenshot.Adapters.LanguageDialogAdapter;
import com.reiserx.screenshot.Advertisements.BannerAds;
import com.reiserx.screenshot.Advertisements.RewardedAds;
import com.reiserx.screenshot.Interfaces.APICallback;
import com.reiserx.screenshot.Interfaces.LanguageSelect;
import com.reiserx.screenshot.Interfaces.RewardedAdCallback;
import com.reiserx.screenshot.Models.Response;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.API;
import com.reiserx.screenshot.Utils.ButtonDesign;
import com.reiserx.screenshot.databinding.ActivityAiactivityBinding;
import com.reiserx.screenshot.databinding.RecyclerDialogBinding;
import com.reiserx.screenshot.databinding.RewardedConsentBinding;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;

public class AIActivity extends AppCompatActivity implements APICallback, LanguageSelect {

    ActivityAiactivityBinding binding;
    Uri uri;
    boolean temp;
    Dialog dialog;
    RewardedAds rewardedAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAiactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rewardedAds = new RewardedAds(this, this);
        rewardedAds.load();

        binding.tv.setTextColor(getColor(R.color.button_design_text));

        binding.progressBar4.setVisibility(View.VISIBLE);
        binding.scrollView2.setVisibility(View.GONE);

        BannerAds bannerAds = new BannerAds(this, binding.adView);
        bannerAds.loadBanner();

        binding.floatingActionButton2.setOnClickListener(view -> {
            dialog();
        });

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            uri = intent.getData();
            temp = intent.getBooleanExtra("temp", false);

            binding.img.setImageURI(uri);
            API api = new API(this, this);
            try {
                api.getAPI(api1 -> {
                    if (api1 != null)
                        api.explainImage(uri, api1.getAPI_KEY(), "English");
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

    @SuppressLint("NotifyDataSetChanged")
    void dialog() {
        // Inflate binding and set up AlertDialog.Builder
        RecyclerDialogBinding binding = RecyclerDialogBinding.inflate(getLayoutInflater());
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(binding.reyclerHolder);

        // Initialize dialog and set up RecyclerView
        dialog = builder.create();
        binding.rec.setLayoutManager(new LinearLayoutManager(this));
        binding.rec.setHasFixedSize(true);

        // Initialize adapter and set data
        ArrayList<String> data = new ArrayList<>();
        LanguageDialogAdapter adapter = new LanguageDialogAdapter(this, data, this);
        binding.rec.setAdapter(adapter);

        // Fetch data from Firebase and update adapter
        FirebaseDatabase.getInstance().getReference("Languages").get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String value = snapshot.getValue(String.class);
                    if (value != null) {
                        data.add(value);
                    }
                }
                if (data.isEmpty()) {
                    Toast.makeText(this, "No languages found", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    binding.progressBar2.setVisibility(View.GONE);
                    binding.rec.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(this, "Failed to get languages", Toast.LENGTH_SHORT).show();
            }
        });

        // Show dialog
        dialog.show();

        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        window.setAttributes(wlp);
    }

    @Override
    public void onLanguageSelected(String language) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();

        RewardedConsentBinding binding = RewardedConsentBinding.inflate(getLayoutInflater());
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(binding.getRoot());
        dialog = builder.create();
        binding.textView29.setText("To explain the screenshot in " + language + ", please watch a short ad to unlock detailed explanation:");

        ButtonDesign design = new ButtonDesign(this);
        design.setButtonOutlineDark(binding.button);
        design.setButtonOutlineDark(binding.button4);

        binding.button.setOnClickListener(view -> {
            design.buttonFillDark(binding.button);
            dialog.dismiss();
            rewardedAds.display(new RewardedAdCallback() {
                @Override
                public void onAdDisplaySuccessful() {
                    callAPI(language);
                }

                @Override
                public void onAdDisplayFailed(String message) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(AIActivity.this);
                    alert.setTitle("Failed to display ad");
                    alert.setMessage(message);
                    alert.setPositiveButton("RETRY", (dialog, which) -> {
                        rewardedAds.load(() -> {
                            rewardedAds.display(this);
                        });
                    });
                    alert.setNegativeButton("CANCEL", null);
                    alert.setCancelable(false);
                    alert.show();
                }
            });
        });

        binding.button4.setOnClickListener(view -> {
            design.buttonFillDark(binding.button4);
            dialog.dismiss();
        });

        // Show dialog
        dialog.show();

        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        window.setAttributes(wlp);
    }

    void callAPI(String language) {
        binding.progressBar4.setVisibility(View.VISIBLE);
        binding.scrollView2.setVisibility(View.GONE);

        API api = new API(this, this);
        try {
            api.getAPI(api1 -> {
                if (api1 != null) {
                    api.explainImage(uri, api1.getAPI_KEY(), language);
                    rewardedAds.load();
                }
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