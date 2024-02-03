package com.reiserx.screenshot.Activities.ui.settings;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.BuildCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.reiserx.screenshot.Interfaces.OnConsentDismissListener;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Services.accessibilityService;
import com.reiserx.screenshot.Utils.BuildConfig;
import com.reiserx.screenshot.Utils.ButtonDesign;
import com.reiserx.screenshot.Utils.DataStoreHelper;
import com.reiserx.screenshot.Utils.isAccessibilityEnabled;
import com.reiserx.screenshot.databinding.FragmentAboutBinding;
import com.reiserx.screenshot.databinding.FragmentConsentBinding;
public class FragmentConsent extends DialogFragment {

    public static final String TAG = "about_dialog";
    FragmentConsentBinding binding;

    ButtonDesign buttonDesign;

    public static int CONSENT_AGREE = 1;
    public static int CONSENT_REJECT = 2;
    public static int CONSENT_DEFAULT = 0;
    public static String CONSENT_KEY = "consent";
    private OnConsentDismissListener onConsentDismissListener;

    public static FragmentConsent display(FragmentManager fragmentManager) {
        FragmentConsent exampleDialog = new FragmentConsent();
        exampleDialog.show(fragmentManager, TAG);
        return exampleDialog;
    }

    public static void setOnConsentDismissListener(FragmentConsent fragment, OnConsentDismissListener listener) {
        if (fragment != null) {
            fragment.setOnConsentDismissListener(listener);
        }
    }

    public void setOnConsentDismissListener(OnConsentDismissListener listener) {
        this.onConsentDismissListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
        setCancelable(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentConsentBinding.inflate(inflater, container, false);

        buttonDesign = new ButtonDesign(getContext());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.textView39.setText(BuildConfig.getVersionName(requireContext()));

        DataStoreHelper dataStoreHelper = new DataStoreHelper();
        int consent = dataStoreHelper.getIntValue(CONSENT_KEY, CONSENT_DEFAULT);

        binding.agree.setOnClickListener(view13 -> {
            buttonDesign.buttonFill(binding.agree);
            dataStoreHelper.putIntValue(CONSENT_KEY, 1);
            dismiss();
            if (onConsentDismissListener != null) {
                onConsentDismissListener.onDismiss(CONSENT_AGREE);
            }
        });

        binding.reject.setOnClickListener(view12 -> {
            buttonDesign.buttonFill(binding.reject);
            dataStoreHelper.putIntValue(CONSENT_KEY, 2);
            dismiss();
            if (onConsentDismissListener != null) {
                onConsentDismissListener.onDismiss(CONSENT_REJECT);
            }
        });

        if (consent == CONSENT_AGREE) {
            buttonDesign.buttonFill(binding.agree);
            buttonDesign.setButtonOutline(binding.reject);
        } else if (consent == CONSENT_REJECT) {
            buttonDesign.buttonFill(binding.reject);
            buttonDesign.setButtonOutline(binding.agree);
        } else {
            buttonDesign.setButtonOutline(binding.agree);
            buttonDesign.setButtonOutline(binding.reject);
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