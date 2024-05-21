package com.reiserx.screenshot.Activities.ui.settings;

import static com.reiserx.screenshot.Activities.ui.settings.FragmentConsent.CONSENT_AGREE;
import static com.reiserx.screenshot.Activities.ui.settings.FragmentConsent.CONSENT_KEY;
import static com.reiserx.screenshot.Activities.ui.settings.FragmentConsent.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Services.ScreenshotTile;
import com.reiserx.screenshot.Services.accessibilityService;
import com.reiserx.screenshot.Utils.DataStoreHelper;
import com.reiserx.screenshot.Utils.isAccessibilityEnabled;
import com.reiserx.screenshot.databinding.FragmentSettingsBinding;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executor;

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

        binding.notificationHolder.setOnClickListener(view15 -> {
            if (binding.notificationSwitch.isChecked()) {
                if (accessibilityService.instance != null) {
                    accessibilityService.instance.cancelNotification(8724);
                    binding.notificationSwitch.setChecked(false);
                }
            } else {
                if (binding.switch1.isChecked()) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        } else {
                            accessibilityService.instance.sendNotification("Capture", "Click to capture screenshot", 8724);
                            binding.notificationSwitch.setChecked(true);
                        }
                    } else {
                        accessibilityService.instance.sendNotification("Capture", "Click to capture screenshot", 8724);
                        binding.notificationSwitch.setChecked(true);
                    }
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Accessibility service is not Active");
                    alert.setMessage("You have not enabled accessibility service\nPlease enable it then try again.");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                }
            }
        });

        binding.quickSettingsHolder.setOnClickListener(view16 -> {
            addTileIfSupported(getContext());
        });
        binding.captureWithSensorHolder.setOnClickListener(view17 -> {
        if (accessibilityService.instance != null) {
                enableSensor();
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Accessibility service is not Active");
            alert.setMessage("You have not enabled accessibility service\nPlease enable it then try again.");
            alert.setPositiveButton("OK", null);
            alert.show();
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
        if (binding.switch1.isChecked()) {
            binding.notificationSwitch.setChecked(accessibilityService.instance.isNotificationActive(8724));
        }
    }

    ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
        } else {
        }
    });

    public void addTileIfSupported(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NavHostFragment.findNavController(this).navigate(R.id.action_navigation_settings_to_navigation_quick_settings);
        } else {
            Toast.makeText(context, "Swipe down/up your notification panel then add quick settings option for this app", Toast.LENGTH_SHORT).show();
        }
    }

    public void enableSensor() {
        NavHostFragment.findNavController(this).navigate(R.id.action_navigation_settings_to_fragmentSensor);
    }

}