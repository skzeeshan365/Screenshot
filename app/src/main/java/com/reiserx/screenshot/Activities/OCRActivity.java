package com.reiserx.screenshot.Activities;

import static com.reiserx.screenshot.MachineLearning.OCR.CHINESE;
import static com.reiserx.screenshot.MachineLearning.OCR.ENGLISH;
import static com.reiserx.screenshot.MachineLearning.OCR.HINDI;
import static com.reiserx.screenshot.MachineLearning.OCR.JAPANESE;
import static com.reiserx.screenshot.MachineLearning.OCR.KOREAN;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.common.InputImage;
import com.reiserx.screenshot.Advertisements.NativeAds;
import com.reiserx.screenshot.Interfaces.ScanCallback;
import com.reiserx.screenshot.MachineLearning.OCR;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.ButtonDesign;
import com.reiserx.screenshot.Utils.DataStoreHelper;
import com.reiserx.screenshot.Utils.ScreenshotUtils;
import com.reiserx.screenshot.databinding.ActivityOcractivityBinding;

import java.util.ArrayList;

public class OCRActivity extends AppCompatActivity {

    ArrayList<String> lang_list;
    ActivityOcractivityBinding binding;

    public static String LANG_CODE = "LANG_CODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOcractivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            Uri uri = intent.getData();

            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.dialog_bottom, null);

            TextView messageTextView = dialogView.findViewById(R.id.dialog_message);

            Button copy_btn = dialogView.findViewById(R.id.copy_btn);
            Button share_btn = dialogView.findViewById(R.id.share_btn);

            Spinner lang_spinner = dialogView.findViewById(R.id.lang_spinner);

            LinearLayout text_holder = dialogView.findViewById(R.id.text_holder);
            ProgressBar progressBar = dialogView.findViewById(R.id.progressBar3);

            text_holder.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            ButtonDesign design = new ButtonDesign(this);
            design.setButtonOutlineDark(copy_btn);
            design.setButtonOutlineDark(share_btn);

            FrameLayout ad_placeholder = dialogView.findViewById(R.id.ad_placeholder);
            NativeAds nativeAds = new NativeAds(this);
            nativeAds.loadAdSmallRunnable(nativeAd -> {
                NativeAds.loadPrefetchedSmall(this, nativeAd, ad_placeholder);
            });

            lang_list = new ArrayList<>();
            lang_list.add("English");
            lang_list.add("हिंदी");
            lang_list.add("한국인");
            lang_list.add("日本語");
            lang_list.add("中国人");

            DataStoreHelper dataStoreHelper = new DataStoreHelper();

            ArrayAdapter<String> subjectsAdapter = new ArrayAdapter<>(this, R.layout.lang_spinner, lang_list);
            lang_spinner.setPopupBackgroundDrawable(getDrawable(R.color.layout_background));
            subjectsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lang_spinner.setAdapter(subjectsAdapter);
            lang_spinner.setSelection(dataStoreHelper.getIntValue(LANG_CODE, 0));

            ScanCallback scanCallback = new ScanCallback() {
                @Override
                public void onScanSuccess(String text) {
                    messageTextView.setText(text.trim());

                    progressBar.setVisibility(View.GONE);
                    text_holder.setVisibility(View.VISIBLE);

                    copy_btn.setOnClickListener(v -> {
                        design.buttonFillDark(copy_btn);
                        copyToClipboard(OCRActivity.this, text);
                        finish();
                    });

                    share_btn.setOnClickListener(view -> {
                        design.buttonFillDark(share_btn);
                        ScreenshotUtils screenshotUtils = new ScreenshotUtils(OCRActivity.this);
                        screenshotUtils.shareText(text);
                        finish();
                    });
                }

                @Override
                public void onScanFailure(String e) {
                    progressBar.setVisibility(View.GONE);
                    text_holder.setVisibility(View.VISIBLE);
                    messageTextView.setText(e);
                }
            };

            OCR ocr = new OCR();
            InputImage inputImage = ocr.prepareImage(OCRActivity.this, uri);

            lang_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedItem = (String) adapterView.getItemAtPosition(i);

                    switch (selectedItem) {
                        case "English":
                            text_holder.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            new Thread(() -> {
                                ocr.setRecognizer(ENGLISH);
                                ocr.scan(inputImage, scanCallback);
                            }).start();
                            dataStoreHelper.putIntValue(LANG_CODE, ENGLISH);
                            break;
                        case "हिंदी":
                            text_holder.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            new Thread(() -> {
                                ocr.setRecognizer(HINDI);
                                ocr.scan(inputImage, scanCallback);
                            }).start();
                            dataStoreHelper.putIntValue(LANG_CODE, HINDI);
                            break;
                        case "한국인":
                            text_holder.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            new Thread(() -> {
                                ocr.setRecognizer(KOREAN);
                                ocr.scan(inputImage, scanCallback);
                            }).start();
                            dataStoreHelper.putIntValue(LANG_CODE, KOREAN);
                            break;
                        case "日本語":
                            text_holder.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            new Thread(() -> {
                                ocr.setRecognizer(JAPANESE);
                                ocr.scan(inputImage, scanCallback);
                            }).start();
                            dataStoreHelper.putIntValue(LANG_CODE, JAPANESE);
                            break;
                        case "中国人":
                            text_holder.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            new Thread(() -> {
                                ocr.setRecognizer(CHINESE);
                                ocr.scan(inputImage, scanCallback);
                            }).start();
                            dataStoreHelper.putIntValue(LANG_CODE, CHINESE);
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            // Create the AlertDialog with the custom view
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            dialog.setOnDismissListener(dialogInterface -> {
                finish();
            });

            dialog.show();

            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            window.setAttributes(wlp);
        }
    }

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clipData = ClipData.newPlainText("Copied Text", text);

        clipboardManager.setPrimaryClip(clipData);

        Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}