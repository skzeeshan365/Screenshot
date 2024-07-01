package com.reiserx.screenshot.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.reiserx.screenshot.Activities.ui.TextDrawable;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.API;
import com.reiserx.screenshot.Utils.BaseApplication;
import com.reiserx.screenshot.Utils.SaveBitmap;
import com.reiserx.screenshot.Utils.ScreenshotUtils;
import com.reiserx.screenshot.databinding.ActivityImageViewerBinding;
import com.stfalcon.imageviewer.StfalconImageViewer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageViewerActivity extends AppCompatActivity {

     ActivityImageViewerBinding binding;
    FloatingActionButton shareFAB, deleteFAB, ocrFAB, aiFAB;
    ExtendedFloatingActionButton mAddFab;
    TextView shareFAB_Text, deleteFAB_Text, ocrFAB_Text, aiFAB_Text;
    Boolean isAllFabsVisible;
    Uri uri;
    StfalconImageViewer stfalconImageViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            uri = intent.getData();
            List<Uri> data = new ArrayList<>();
            data.add(uri);

            Context materialContext = new ContextThemeWrapper(this, R.style.FullscreenTheme);
            LayoutInflater inflater = LayoutInflater.from(materialContext);

            View customView = inflater.inflate(R.layout.image_viewer_overlay, null, false);
            customViewOps(customView);
            stfalconImageViewer = new StfalconImageViewer.Builder<>( ImageViewerActivity.this, data, (imageView, imageUrl) -> Glide.with(this).load(imageUrl).into(imageView))
                    .withHiddenStatusBar(true)
                    .withOverlayView(customView)
                    .withDismissListener(this::finish)
                    .show();
            hideSystemUI(customView.getWindowInsetsController());
        }
    }

    void customViewOps(View view) {
        mAddFab = view.findViewById(R.id.add_fab);
        shareFAB = view.findViewById(R.id.fab_full);
        deleteFAB = view.findViewById(R.id.fab_partial);
        shareFAB_Text = view.findViewById(R.id.share_image_text);
        deleteFAB_Text = view.findViewById(R.id.delete_image_text);
        ocrFAB = view.findViewById(R.id.fab_snapshot);
        ocrFAB_Text = view.findViewById(R.id.ocr_image_text);
        aiFAB = view.findViewById(R.id.fab_silent);
        aiFAB_Text = view.findViewById(R.id.ai_image_text);
        ConstraintLayout overlay_holder = view.findViewById(R.id.overlay_holder);

        shareFAB.setVisibility(View.GONE);
        deleteFAB.setVisibility(View.GONE);
        shareFAB_Text.setVisibility(View.GONE);
        deleteFAB_Text.setVisibility(View.GONE);
        ocrFAB.setVisibility(View.GONE);
        ocrFAB_Text.setVisibility(View.GONE);
        aiFAB.setVisibility(View.GONE);
        aiFAB_Text.setVisibility(View.GONE);

        TextDrawable textDrawable = new TextDrawable("T");
        textDrawable.setTextColor(this.getColor(R.color.white));
        textDrawable.setTextSize(50);
        textDrawable.setFont(this, R.font.source_serif_pro_semibold);
        ocrFAB.setImageDrawable(textDrawable);

        textDrawable = new TextDrawable("AI");
        textDrawable.setTextColor(this.getColor(R.color.white));
        textDrawable.setTextSize(50);
        textDrawable.setFont(this, R.font.source_serif_pro_semibold);
        aiFAB.setImageDrawable(textDrawable);

        isAllFabsVisible = false;

        mAddFab.shrink();

        mAddFab.setOnClickListener(view13 -> {
            if (!isAllFabsVisible) {
                shareFAB.show();
                deleteFAB.show();
                ocrFAB.show();
                aiFAB.show();
                shareFAB_Text.setVisibility(View.VISIBLE);
                deleteFAB_Text.setVisibility(View.VISIBLE);
                ocrFAB_Text.setVisibility(View.VISIBLE);
                aiFAB_Text.setVisibility(View.VISIBLE);

                overlay_holder.setBackgroundColor(getColor(R.color.overlay_background));

                mAddFab.extend();

                isAllFabsVisible = true;
            } else {
                shareFAB.hide();
                deleteFAB.hide();
                ocrFAB.hide();
                aiFAB.hide();
                shareFAB_Text.setVisibility(View.GONE);
                deleteFAB_Text.setVisibility(View.GONE);
                ocrFAB_Text.setVisibility(View.GONE);
                aiFAB_Text.setVisibility(View.GONE);

                overlay_holder.setBackgroundColor(Color.TRANSPARENT);

                mAddFab.shrink();

                isAllFabsVisible = false;
            }
        });

        shareFAB.setOnClickListener(view1 -> BaseApplication.getInterstitialAds().displayAd(this, () -> {
            ScreenshotUtils screenshotUtils = new ScreenshotUtils(this);
            screenshotUtils.shareImage(API.getFileFromUri(uri, this));
        }));

        deleteFAB.setOnClickListener(view12 -> BaseApplication.getInterstitialAds().displayAd(this, this::deleteImage));

        ocrFAB.setOnClickListener(view14 -> BaseApplication.getInterstitialAds().displayAd(this, () -> {
            Intent intent = new Intent(this, OCRActivity.class);
            intent.setData(uri);
            startActivity(intent);
        }));

        aiFAB.setOnClickListener(view15 -> BaseApplication.getInterstitialAds().displayAd(this, () -> {
            Intent intent = new Intent(this, AIActivity.class);
            intent.setData(uri);
            startActivity(intent);
        }));
    }

    void deleteImage() {
        File file = API.getFileFromUri(uri, this);
        if (file != null && file.exists()) {
            int result = ScreenshotUtils.getFileLocationType(this, file);
            if (result == ScreenshotUtils.APP_DATA_DIR) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Delete screenshot");
                alert.setMessage("Are you sure you want to delete this screenshot?");
                alert.setPositiveButton("delete", (dialogInterface, i) -> {
                    if (file.delete()) {
                        stfalconImageViewer.close();
                    }
                });
                alert.setNegativeButton("cancel", null);
                alert.show();
            } else {
                ArrayList<Uri> arrayList = new ArrayList<>();
                arrayList.add(Uri.parse(file.getAbsolutePath()));
                SaveBitmap.deleteScreenshotDCIM(this, arrayList, deleteResultLauncher);
            }
        }
    }

    ActivityResultLauncher<IntentSenderRequest> deleteResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK){
                        Toast.makeText(ImageViewerActivity.this, "Screenshot deleted.", Toast.LENGTH_SHORT).show();
                        stfalconImageViewer.close();
                    }
                }
            }
    );

    public static void hideSystemUI(WindowInsetsController controller) {

        if (controller != null) {
            // Hide both the status bar and the navigation bar
            controller.hide(WindowInsets.Type.systemBars());

            // Set the behavior to allow the system bars to be shown with a swipe gesture
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }
}