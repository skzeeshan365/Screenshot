package com.reiserx.screenshot.Activities.ui.settings;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.os.BuildCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Services.accessibilityService;
import com.reiserx.screenshot.Utils.ButtonDesign;
import com.reiserx.screenshot.Utils.isAccessibilityEnabled;
import com.reiserx.screenshot.databinding.FragmentAboutBinding;

public class FragmentAbout extends DialogFragment {

    public static final String TAG = "about_dialog";
    FragmentAboutBinding binding;

    ButtonDesign buttonDesign;

    public static FragmentAbout display(FragmentManager fragmentManager) {
        FragmentAbout exampleDialog = new FragmentAbout();
        exampleDialog.show(fragmentManager, TAG);
        return exampleDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentAboutBinding.inflate(inflater, container, false);

        buttonDesign = new ButtonDesign(getContext());
        buttonDesign.setButtonOutline(binding.button);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.imageView6.setOnClickListener(view1 -> {
          dismiss();
        });

        isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(getContext());

        if (isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class)) {
            binding.button.setEnabled(false);
            binding.button.setText("PERMISSION GRANTED");
        } else {
            binding.button.setOnClickListener(view12 -> {
                buttonDesign.buttonFill(binding.button);
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                dismiss();
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.Slide);
        }
    }
}