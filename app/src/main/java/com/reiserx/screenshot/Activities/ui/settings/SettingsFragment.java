package com.reiserx.screenshot.Activities.ui.settings;

import static com.reiserx.screenshot.Activities.ui.settings.FragmentConsent.CONSENT_AGREE;
import static com.reiserx.screenshot.Activities.ui.settings.FragmentConsent.CONSENT_KEY;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.ads.nativead.NativeAd;
import com.reiserx.screenshot.Advertisements.BannerAds;
import com.reiserx.screenshot.Advertisements.NativeAds;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Services.accessibilityService;
import com.reiserx.screenshot.Utils.DataStoreHelper;
import com.reiserx.screenshot.Utils.isAccessibilityEnabled;
import com.reiserx.screenshot.databinding.FragmentSettingsBinding;

import java.util.List;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    DataStoreHelper dataStoreHelper;

    public static String SCREENSHOT_TYPE_KEY = "SCREENSHOT_TYPE_KEY";
    public static int DEFAULT = 0;
    public static int SCREENSHOT = 1;
    public static int SILENT_SCREENSHOT = 2;
    public static int SNAPSHOT = 3;
    public static int SNAPSHOT_TYPE_OCR = 4;
    public static int SNAPSHOT_TYPE_AI = 5;
    public static int CURRENT_WINDOW = 6;

    public static String DOUBLE_TAP_ENABLE = "DOUBLE_TAP_ENABLE";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshAccessibilityPermission();

        dataStoreHelper = new DataStoreHelper();

        binding.switchHolder.setOnClickListener(view1 -> {
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

        BannerAds ads = new BannerAds(getContext(), binding.adPlaceholder);
        ads.loadBanner();

        NativeAds nativeAds = new NativeAds(getContext());
        nativeAds.prefetchAds(2, () -> {
            List<NativeAd> nativeAds1 = nativeAds.getAdList();
            if (!nativeAds1.isEmpty()) {
                if (nativeAds1.size() > 1) {
                    NativeAds.loadNoIconPrefetchedAds(requireContext(), nativeAds1.get(0), binding.adPlaceholder1);
                    NativeAds.loadNoIconPrefetchedAds(requireContext(), nativeAds1.get(1), binding.adPlaceholder2);
                } else {
                    NativeAds.loadNoIconPrefetchedAds(requireContext(), nativeAds1.get(0), binding.adPlaceholder1);
                }
            }
        });

        updateValues();

        binding.screenshotTypeHolder.setOnClickListener(view18 -> {
            setScreenshotType(getContext());
        });

        binding.doubleTapHolder.setOnClickListener(view19 -> {
            binding.switch2.setChecked(!binding.switch2.isChecked());
        });

        binding.switch2.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                if (binding.switch1.isChecked()) {
                    dataStoreHelper.putBooleanValue(DOUBLE_TAP_ENABLE, b);
                } else {
                    binding.switch2.setChecked(false);
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Accessibility service is not Active");
                    alert.setMessage("You have not enabled accessibility service\nPlease enable it then try again.");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                }
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

    public void setScreenshotType(Context context) {

        AlertDialog alert = new AlertDialog.Builder(context).create();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.screenshot_types, null);

        final RadioButton btn1 = mView.findViewById(R.id.radioButton);
        final RadioButton btn2 = mView.findViewById(R.id.radioButton2);
        final RadioButton btn3 = mView.findViewById(R.id.radioButton3);
        final RadioButton btn4 = mView.findViewById(R.id.radioButton4);
        final RadioButton ocr = mView.findViewById(R.id.rad_ocr);
        final RadioButton ai = mView.findViewById(R.id.rad_ai);
        final RadioButton current_window = mView.findViewById(R.id.current_window);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            current_window.setVisibility(View.GONE);
        }

        alert.setTitle("Select screenshot type");
        alert.setMessage("Select screenshot type for capturing with sensor");
        alert.setView(mView);

        if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == DEFAULT)
            btn1.setChecked(true);
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SCREENSHOT)
            btn2.setChecked(true);
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SILENT_SCREENSHOT)
            btn3.setChecked(true);
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SNAPSHOT)
            btn4.setChecked(true);
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SNAPSHOT_TYPE_OCR)
            ocr.setChecked(true);
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SNAPSHOT_TYPE_AI)
            ai.setChecked(true);
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == CURRENT_WINDOW)
            current_window.setChecked(true);

        btn1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn1.isChecked()) {
                dataStoreHelper.putIntValue(SCREENSHOT_TYPE_KEY, DEFAULT);
            }
            alert.dismiss();
            updateValues();
        });

        btn2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn2.isChecked()) {
                dataStoreHelper.putIntValue(SCREENSHOT_TYPE_KEY, SCREENSHOT);
            }
            alert.dismiss();
            updateValues();
        });

        btn3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn3.isChecked()) {
                dataStoreHelper.putIntValue(SCREENSHOT_TYPE_KEY, SILENT_SCREENSHOT);
            }
            alert.dismiss();
            updateValues();
        });
        btn4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn4.isChecked()) {
                dataStoreHelper.putIntValue(SCREENSHOT_TYPE_KEY, SNAPSHOT);
            }
            alert.dismiss();
            updateValues();
        });
        ocr.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (ocr.isChecked()) {
                dataStoreHelper.putIntValue(SCREENSHOT_TYPE_KEY, SNAPSHOT_TYPE_OCR);
            }
            alert.dismiss();
            updateValues();
        });
        ai.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (ai.isChecked()) {
                dataStoreHelper.putIntValue(SCREENSHOT_TYPE_KEY, SNAPSHOT_TYPE_AI);
            }
            alert.dismiss();
            updateValues();
        });
        current_window.setOnCheckedChangeListener((compoundButton, b) -> {
            if (current_window.isChecked()) {
                dataStoreHelper.putIntValue(SCREENSHOT_TYPE_KEY, CURRENT_WINDOW);
            }
            alert.dismiss();
            updateValues();
        });
        alert.show();
    }

    void updateValues() {
        if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == DEFAULT)
            binding.screenshotTypeValue.setText("Ask every time");
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SCREENSHOT)
            binding.screenshotTypeValue.setText(getString(R.string.full_screen));
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SILENT_SCREENSHOT)
            binding.screenshotTypeValue.setText(getString(R.string.silent));
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SNAPSHOT)
            binding.screenshotTypeValue.setText(getString(R.string.selected_screenshot_label));
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SNAPSHOT_TYPE_OCR)
            binding.screenshotTypeValue.setText(getString(R.string.capture_with_ocr));
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == SNAPSHOT_TYPE_AI)
            binding.screenshotTypeValue.setText(getString(R.string.capture_with_ai_explain));
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == CURRENT_WINDOW)
            binding.screenshotTypeValue.setText(getString(R.string.current_app));
    }
}