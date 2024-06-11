package com.reiserx.screenshot.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ortiz.touchview.TouchImageView;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.ScreenshotUtils;
import com.reiserx.screenshot.databinding.ActivityImageViewerBinding;

import java.io.File;

public class ImageViewerActivity extends AppCompatActivity {

     ActivityImageViewerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String Url = getIntent().getStringExtra("url");

        TouchImageView img = findViewById(R.id.imageView6);

        Glide.with(this).load(Url)
                .into(img);
        binding.floatingActionButton.setOnClickListener(view -> {
            if (Url != null) {
                ScreenshotUtils utils = new ScreenshotUtils(this);
                utils.shareImage(new File(Url));
            }
        });
    }
}