package com.reiserx.screenshot.Activities;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.ButtonDesign;
import com.reiserx.screenshot.Utils.ScreenshotUtils;

public class OCRActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocractivity);

        String message = getIntent().getStringExtra("message");

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_bottom, null);

        TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
        messageTextView.setText(message);

        Button copy_btn = dialogView.findViewById(R.id.copy_btn);
        Button share_btn = dialogView.findViewById(R.id.share_btn);

        ButtonDesign design = new ButtonDesign(this);
        design.setButtonOutlineDark(copy_btn);
        design.setButtonOutlineDark(share_btn);

        copy_btn.setOnClickListener(v -> {
            design.buttonFillDark(copy_btn);
            copyToClipboard(this, message);
            finish();
        });

        share_btn.setOnClickListener(view -> {
            design.buttonFillDark(share_btn);
            ScreenshotUtils screenshotUtils = new ScreenshotUtils(this);
            screenshotUtils.shareText(message);
            finish();
        });

        // Create the AlertDialog with the custom view
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialog.show();

        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
    }

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clipData = ClipData.newPlainText("Copied Text", text);

        clipboardManager.setPrimaryClip(clipData);

        Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}