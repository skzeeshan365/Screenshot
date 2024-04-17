package com.reiserx.screenshot.Activities.ui.settings;

import static com.reiserx.screenshot.Activities.ui.settings.FragmentConsent.CONSENT_AGREE;
import static com.reiserx.screenshot.Activities.ui.settings.FragmentConsent.CONSENT_KEY;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.reiserx.screenshot.Services.accessibilityService;
import com.reiserx.screenshot.Utils.DataStoreHelper;
import com.reiserx.screenshot.Utils.isAccessibilityEnabled;
import com.reiserx.screenshot.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshAccessibilityPermission();

        binding.switchHolder.setOnClickListener(view1 -> {
            DataStoreHelper dataStoreHelper = new DataStoreHelper();
            if (dataStoreHelper.getIntValue(CONSENT_KEY, 0) == CONSENT_AGREE)
                FragmentAbout.display(requireActivity().getSupportFragmentManager());
            else {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Consent required");
                alert.setMessage("You have not agreed to consent required for accessibility service\nPlease click on consent to open consent dialog.");
                alert.setPositiveButton("consent", (dialogInterface, i) -> FragmentConsent.display(requireActivity().getSupportFragmentManager()));
                alert.setNegativeButton("cancel", null);
                alert.show();
            }
        });

        binding.defaultStorageValue.setText(FileFragment.PRIMARY_DEFAULT_STORAGE.concat("/".concat(new DataStoreHelper().getStringValue(FileFragment.DEFAULT_STORAGE_KEY, null))));

        binding.defaultStorageHolder.setOnClickListener(view12 -> {
            FileFragment fileFragment = FileFragment.display(requireActivity().getSupportFragmentManager());
            FileFragment.setOnFileDismissListener(fileFragment, voids -> binding.defaultStorageValue.setText(FileFragment.PRIMARY_DEFAULT_STORAGE.concat("/".concat(new DataStoreHelper().getStringValue(FileFragment.DEFAULT_STORAGE_KEY, null)))));
        });

        binding.rate.setOnClickListener(view13 -> {
            String appUrl = "https://play.google.com/store/apps/details?id=com.reiserx.screenshot";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                // If no app can handle the Intent, do nothing or handle the error as needed
            }
        });

        binding.website.setOnClickListener(view14 -> {
            String url = "https://reiserx.com";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                // Handle the exception if the device does not have a web browser installed
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAccessibilityPermission();
    }

    void refreshAccessibilityPermission() {
        isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(getContext());

        binding.switch1.setChecked(isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class));
    }
}