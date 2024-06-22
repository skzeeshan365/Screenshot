package com.reiserx.screenshot.Activities.ui.settings;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.reiserx.screenshot.Advertisements.NativeAds;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Services.accessibilityService;
import com.reiserx.screenshot.Utils.DataStoreHelper;
import com.reiserx.screenshot.databinding.FragmentSensorBinding;

public class FragmentSensor extends Fragment {

    private FragmentSensorBinding binding;

    public static String SENSOR_KEY = "SENSOR_KEY";
    public static String SHAKE_COUNT = "SHAKE_COUNT";
    DataStoreHelper dataStoreHelper;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSensorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataStoreHelper = new DataStoreHelper();

        if (accessibilityService.instance != null) {
            binding.enableSwitch.setChecked(accessibilityService.instance.isProximitySensorEnabled());
            binding.enableShakeSwitch.setChecked(accessibilityService.instance.isShakeSensorEnabled());
        } else {
            binding.enableSwitch.setChecked(false);
            binding.enableShakeSwitch.setChecked(false);
        }

        binding.enableShakeSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (accessibilityService.instance != null) {
                if (b) {
                    accessibilityService.instance.enableShakeDetection();
                    binding.captureWithShakeSensorHolder.setEnabled(true);
                } else {
                    accessibilityService.instance.disableShakeDetection();
                    binding.captureWithShakeSensorHolder.setEnabled(false);
                }
            }
        });

        binding.captureWithShakeSensorHolder.setOnClickListener(view12 -> {
            setSensorCount(getContext());
        });

        binding.enableSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (accessibilityService.instance != null) {
                if (b) {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE);
                    } else {
                        accessibilityService.instance.enableProximitySensor();
                        dataStoreHelper.putBooleanValue(SENSOR_KEY, b);
                    }
                } else {
                    accessibilityService.instance.disableProximitySensor();
                    dataStoreHelper.putBooleanValue(SENSOR_KEY, b);
                }
            }
        });

        binding.captureWithSensorHolder.setOnClickListener(view1 -> {
//            setScreenshotType(getContext());
        });

        binding.captureWithShakeSensorHolder.setEnabled(binding.enableShakeSwitch.isChecked());

        binding.shakeSensorValue.setText(String.valueOf(dataStoreHelper.getIntValue(SHAKE_COUNT, 1)));

        NativeAds nativeAds = new NativeAds(getContext(), binding.adPlaceholder);
        nativeAds.loadAdLarge();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void setSensorCount(Context context) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.number_picker, null);

        final NumberPicker picker = mView.findViewById(R.id.numberpicker_main_picker);

        alert.setTitle("Shake count");
        alert.setMessage("Set shake count for capturing with Shake");
        alert.setView(mView);

        picker.setMaxValue(5);
        picker.setMinValue(1);

        picker.setValue(dataStoreHelper.getIntValue(SHAKE_COUNT, 1));

        alert.setPositiveButton("set", (dialogInterface, i) -> {
            int select_value = picker.getValue();
            binding.shakeSensorValue.setText(String.valueOf(select_value));
            dataStoreHelper.putIntValue(SHAKE_COUNT, select_value);
        });
        alert.setNegativeButton("cancel", null);

        AlertDialog builder = alert.create();
        builder.show();
    }

    ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            accessibilityService.instance.enableProximitySensor();
            dataStoreHelper.putBooleanValue(SENSOR_KEY, true);
        } else {
            Toast.makeText(getContext(), "Please grant necessary permissions", Toast.LENGTH_SHORT).show();
        }
    });
}